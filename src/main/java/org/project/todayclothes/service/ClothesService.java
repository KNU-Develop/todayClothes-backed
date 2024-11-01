package org.project.todayclothes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.entity.Clothe;
import org.project.todayclothes.repository.ClotheRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClothesService {
    private final ClotheRepository clotheRepository;
    private final S3UploadService s3UploadService;
    private final BackgroundRemoverService backgroundRemoverService;

    @Transactional
    public void saveClothe(Clothe clothe) {
        clotheRepository.save(clothe);
    }
    public void processAndUploadImageBatchWithDelay(List<Clothe> clothes) {
        List<String> downloadedPaths = new ArrayList<>();
        List<String> processedPaths = new ArrayList<>();

        for (int i = 0; i < clothes.size(); i++) {
            Clothe clothe = clothes.get(i);
            if (s3UploadService.shouldSkipProcessing(clothe.getImgUrl())) {
                continue;
            }

            String downloadedImagePath = null;
            String processedImagePath = null;
            try {
                downloadedImagePath = backgroundRemoverService.downloadImage(clothe.getImgUrl());
                processedImagePath = backgroundRemoverService.removeBackground(downloadedImagePath);

                downloadedPaths.add(downloadedImagePath);
                processedPaths.add(processedImagePath);

                String newImageUrl = s3UploadService.savePhoto(processedImagePath, clothe.getImgUrl());

                clothe.updateImage(newImageUrl);
                saveClothe(clothe);

            } catch (IOException e) {
                log.error("Image processing failed for URL: {}", clothe.getImgUrl(), e);
            }
            if ((i + 1) % 10 == 0) {
                try {
                    TimeUnit.SECONDS.sleep(30); // Adjust delay as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Interrupted while waiting", e);
                }
                deleteLocalFiles(downloadedPaths);
                deleteLocalFiles(processedPaths);
                downloadedPaths.clear();
                processedPaths.clear();
            }
        }
        deleteLocalFiles(downloadedPaths);
        deleteLocalFiles(processedPaths);
    }
    public void deleteLocalFiles(List<String> filePaths) {
        for (String filePath : filePaths) {
            deleteLocalFile(filePath);
        }
    }

    public void deleteLocalFile(String filePath) {
        Path path = Paths.get(filePath);
        try {
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("Successfully deleted local file: {}", path);
            } else {
                log.warn("File does not exist, cannot delete: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error occurred while deleting the file: {}", filePath, e);
        }
    }
    public void saveClothesBatch(List<Clothe> clotheBatch) {
        clotheRepository.saveAll(clotheBatch);  // 일괄 저장
    }

    @Transactional
    public void saveClotheDate(List<ClotheDto> clotheDtoList){
        for (ClotheDto clotheDto : clotheDtoList) {
            clotheRepository.save(new Clothe(clotheDto));
        }
    }
}