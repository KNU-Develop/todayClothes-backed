package org.project.todayclothes.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ClotheErrorCode;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class S3UploadService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String CLOTHES_IMG_DIR = "kream/";
    private static final String REVIEW_IMG_DIR = "reviews/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    private static final String TRANSPARENT_BG_IMAGE_URL = "https://static.zara.net/stdstatic/6.34.2/images/transparent-background.png";

    public boolean shouldSkipProcessing(String imgUrl) {
        return TRANSPARENT_BG_IMAGE_URL.equals(imgUrl);
    }

    public String savePhoto(MultipartFile multipartFile) {
        validateFile(multipartFile);
        String fileName = REVIEW_IMG_DIR + UUID.randomUUID() + multipartFile.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new BusinessException(ReviewErrorCode.S3_UPLOAD_FAILED);
        }

        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public String savePhoto(String filePath, String imgUrl) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new BusinessException(ClotheErrorCode.S3_UPLOAD_FAILED);
        }
        String fileName = CLOTHES_IMG_DIR + imgUrl.substring(imgUrl.lastIndexOf('/') + 1);

        try (FileInputStream inputStream = new FileInputStream(file)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.length());
            metadata.setContentType("image/png");

            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            log.info("Successfully uploaded to S3: {}", fileName);

            if (file.delete()) {
                log.info("Local file deleted: {}", filePath);
            } else {
                log.warn("Failed to delete local file: {}", filePath);
            }

            return amazonS3Client.getUrl(bucket, fileName).toString();
        } catch (IOException e) {
            throw new BusinessException(ClotheErrorCode.S3_UPLOAD_FAILED);
        }
    }


    private String extractFileName(String imageUrl) {
        return "processed_" + imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }


    public void deletePhoto(String imageUrl) {
        String fileName = extractFileNameFromUrl(imageUrl);

        try {
            amazonS3Client.deleteObject(bucket, fileName);
        } catch (Exception e) {
            throw new BusinessException(ReviewErrorCode.S3_DELETE_FAILED);
        }
    }

    private String extractFileNameFromUrl(String imageUrl) {
        int dirIndex = imageUrl.indexOf(CLOTHES_IMG_DIR);
        if (dirIndex != -1) {
            return imageUrl.substring(dirIndex + CLOTHES_IMG_DIR.length());
        }
        // CLOTHES_IMG_DIR가 포함되지 않은 경우 기본적으로 URL의 마지막 파일명을 추출
        return imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }



    private void validateFile(MultipartFile multipartFile) {
        if (multipartFile.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ReviewErrorCode.FILE_SIZE_EXCEEDED);
        }

        if (!multipartFile.getContentType().startsWith("image/")) {
            throw new BusinessException(ReviewErrorCode.INVALID_FILE_FORMAT);
        }
    }
}
