package org.project.todayclothes;

import org.junit.jupiter.api.Test;
import org.project.todayclothes.service.S3UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class S3UploadTest {
    @Autowired
    private S3UploadService s3UploadService;

    @Test
    void testUploadImageToS3() throws IOException {
        String filePath = "src/test/resources/GDG.png";
        FileInputStream inputFile = new FileInputStream(filePath);

        MultipartFile multipartFile = new MockMultipartFile("file", "GDG.png", "image/png", inputFile);

        String imageUrl = s3UploadService.savePhoto(multipartFile);

        assertNotNull(imageUrl);
        System.out.println("S3에 업로드된 이미지 URL: " + imageUrl);
    }
}
