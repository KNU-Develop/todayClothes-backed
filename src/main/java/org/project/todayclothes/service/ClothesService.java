package org.project.todayclothes.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.component.ImageProcessor;
import org.project.todayclothes.dto.*;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.dto.gpt.GptResClotheRecommendDto;
import org.project.todayclothes.entity.Clothe;
import org.project.todayclothes.repository.ClotheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ClothesService {
    @Value("${ai.recommend-clothe-url}")
    private String aiServerUrl;

    private final ClotheRepository clotheRepository;
    private final S3UploadService s3UploadService;
    private final BackgroundRemoverService backgroundRemoverService;
    private final RestTemplate restTemplate;
    private final ImageProcessor imageProcessor;


    public String generateS3Url(String siteName, String originalImageUrl) throws UnsupportedEncodingException {
        String fileName;
        String folderName = siteName.toLowerCase() + "/";

        switch (siteName.toLowerCase()) {
            case "kream":
                fileName = originalImageUrl.substring(originalImageUrl.lastIndexOf('/') + 1);
                int queryIndex = fileName.indexOf("?");
                if (queryIndex != -1) {
                    String baseFileName = fileName.substring(0, queryIndex);
                    String queryPart = fileName.substring(queryIndex);
                    fileName = baseFileName + URLEncoder.encode(queryPart, "UTF-8");
                }
                break;

            case "kappy":
                fileName = originalImageUrl.substring(originalImageUrl.lastIndexOf('/') + 1).replaceAll("\\?.*", "");
                if (!fileName.endsWith(".jpg")) {
                    fileName = fileName.replaceAll("\\..*$", "") + ".png";
                }
                break;

            default:
                fileName = originalImageUrl.substring(originalImageUrl.lastIndexOf('/') + 1);
                break;
        }

        return "https://todayclothes-file.s3.ap-northeast-2.amazonaws.com/" + folderName + fileName;
    }

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

    public ResFinalRecommendClotheDto recommendClothe(ReqRecommendClotheDto clotheDto) throws IOException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<ReqRecommendClotheDto> request = new HttpEntity<>(clotheDto, headers);

            ResponseEntity<ResRecommendClotheDto> response = restTemplate.postForEntity(aiServerUrl, request, ResRecommendClotheDto.class);
            String recommendImageUrl = imageProcessor.getRecommendImageUrl(response.getBody());

            return ResFinalRecommendClotheDto.builder()
                    .recommendClotheUrl(recommendImageUrl)
                    .comment(Objects.requireNonNull(response.getBody()).getComment())
                    .build();
        } catch (Exception e) {
            log.error("Failed to recommend clothe", e);
            throw new IOException("Failed to recommend clothe");
        }
    }

    @Transactional(readOnly = true)
    public ClotheRecommendDto getS3ImageUrlById(GptResClotheRecommendDto gptResClotheRecommendDto) {
        List<Long> ids = Arrays.asList(
                gptResClotheRecommendDto.getTop(),
                gptResClotheRecommendDto.getBottom(),
                gptResClotheRecommendDto.getShoes(),
                gptResClotheRecommendDto.getAcc1(),
                gptResClotheRecommendDto.getAcc2()
        );
        int lastIdx = ids.size() - 1;
        if (ids.get(lastIdx) == null) {
            ids.set(lastIdx, gptResClotheRecommendDto.getOuter());
        }

        Map<Long, String> imgUrlMap = clotheRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Clothe::getId, Clothe::getImgUrl));


        return ClotheRecommendDto.builder()
                .top(imgUrlMap.getOrDefault(gptResClotheRecommendDto.getTop(), null))
                .bottom(imgUrlMap.getOrDefault(gptResClotheRecommendDto.getBottom(), null))
                .shoes(imgUrlMap.getOrDefault(gptResClotheRecommendDto.getShoes(), null))
                .acc1(imgUrlMap.getOrDefault(gptResClotheRecommendDto.getAcc1(), null))
                .acc2(imgUrlMap.getOrDefault(gptResClotheRecommendDto.getAcc2(), String.valueOf(gptResClotheRecommendDto.getOuter())))
                .comment(gptResClotheRecommendDto.getComment())
                .build();
    }
}