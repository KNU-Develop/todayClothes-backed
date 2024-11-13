package org.project.todayclothes.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.project.todayclothes.dto.S3UploadDto;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.CommonErrorCode;
import org.project.todayclothes.utils.S3ConverterUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class S3ImageService {
    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadImage(Object image, String fileName) {
        S3UploadDto s3UploadDto;
        try {
            s3UploadDto = S3ConverterUtil.convertImageToS3UploadDto(image);
        } catch (IOException e) {
            throw new BusinessException(CommonErrorCode.FILE_CONVERT_FAIL);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(s3UploadDto.getContentLength());
        metadata.setContentType(s3UploadDto.getContentType());
        String s3FilePath = "clothes/" + fileName;
        s3Client.putObject(new PutObjectRequest(bucketName, s3FilePath, s3UploadDto.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        return s3Client.getUrl(bucketName, s3FilePath).toString();
    }
}
