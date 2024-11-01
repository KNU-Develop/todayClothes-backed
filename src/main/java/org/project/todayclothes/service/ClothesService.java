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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClothesService {
    private final ClotheRepository clotheRepository;
    private final S3UploadService s3UploadService;
    private final BackgroundRemoverService backgroundRemoverService;

    public void processAndUploadImage(Clothe clothe) throws IOException {
        if (s3UploadService.shouldSkipProcessing(clothe.getImgUrl())) {
            return;
        }

        String downloadedImagePath = null;
        String processedImagePath = null;
        try {

            downloadedImagePath = backgroundRemoverService.downloadImage(clothe.getImgUrl());

            processedImagePath = backgroundRemoverService.removeBackground(downloadedImagePath);

            String newImageUrl = s3UploadService.savePhoto(processedImagePath);

            clothe.updateImage(newImageUrl);
            clotheRepository.save(clothe);

        } finally {
            if (downloadedImagePath != null) {
                deleteLocalFile(downloadedImagePath);
            }
            if (processedImagePath != null) {
                deleteLocalFile(processedImagePath);
            }
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

            if (Files.exists(path)) {
                log.error("Failed to delete file: {}", filePath);
            } else {
                log.info("File deletion confirmed: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Error occurred while deleting the file: {}", filePath, e);
        }
    }

    @Transactional
    public void saveClotheDate(List<ClotheDto> clotheDtoList){
        for (ClotheDto clotheDto : clotheDtoList) {
            clotheRepository.save(new Clothe(clotheDto));
        }
    }
}