package org.project.todayclothes.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private static final String CLOTHES_IMG_DIR = "clothes/";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public String savePhoto(MultipartFile multipartFile) {
        validateFile(multipartFile);
        String fileName = CLOTHES_IMG_DIR + UUID.randomUUID() + multipartFile.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(multipartFile.getSize());
            metadata.setContentType(multipartFile.getContentType());

            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, multipartFile.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new BusinessException(ReviewErrorCode.S3_UPLOAD_FAILED);
        }

        return amazonS3Client.getUrl(bucket, fileName).toString(); // 업로드된 파일 URL 반환
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
        return imageUrl.substring(imageUrl.indexOf(CLOTHES_IMG_DIR));
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
