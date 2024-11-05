package org.project.todayclothes.utils;

import org.project.todayclothes.dto.S3UploadDto;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.CommonErrorCode;
import org.project.todayclothes.exception.code.ReviewErrorCode;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class S3ConverterUtil {
    private static final String FORMAT_PNG = "image/png";
    private static final int S3_MAX_FILE_SIZE = 5 * 1024 * 1024;

    public static S3UploadDto convertImageToS3UploadDto(Object image) throws IOException {
        if (image instanceof BufferedImage) {
            return convertBufferedImageToS3Dto((BufferedImage) image);
        } else if (image instanceof File) {
            return convertFileToS3Dto((File) image);
        } else if (image instanceof MultipartFile) {
            return convertMultipartFileToS3Dto((MultipartFile) image);
        } else {
            throw new BusinessException(CommonErrorCode.FILE_CONVERT_FAIL);
        }
    }

    private static void validateFileSize(long size) {
        if (size > S3_MAX_FILE_SIZE) {
            throw new BusinessException(ReviewErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private static S3UploadDto convertBufferedImageToS3Dto(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        validateFileSize(outputStream.size());
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return new S3UploadDto(inputStream, outputStream.size(), FORMAT_PNG);
    }

    private static S3UploadDto convertFileToS3Dto(File file) throws IOException {
        validateFileSize(file.length());
        InputStream inputStream = new FileInputStream(file);
        return new S3UploadDto(inputStream, file.length(), FORMAT_PNG);
    }

    private static S3UploadDto convertMultipartFileToS3Dto(MultipartFile multipartFile) throws IOException {
        validateFileSize(multipartFile.getSize());
        InputStream inputStream = multipartFile.getInputStream();
        return new S3UploadDto(inputStream, multipartFile.getSize(), multipartFile.getContentType());
    }
}
