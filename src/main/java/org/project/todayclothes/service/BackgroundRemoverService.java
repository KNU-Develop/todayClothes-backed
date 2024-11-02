package org.project.todayclothes.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ClotheErrorCode;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class BackgroundRemoverService {

    private final S3UploadService s3UploadService;

    private static final String PYTHON_SCRIPT_PATH = "C:\\deploy\\todayClothes-backed\\remove_bg.py";
    //private static final String PYTHON_SCRIPT_PATH = "/app/scripts/remove_bg.py";
    private static final String TEMP_DIR = "C:\\deploy\\todayClothes-backed\\src\\main\\resources\\img";


    public String processImageAndUploadToS3(String imageUrl) {
        try {
            String processedImagePath = removeBackground(imageUrl);
            log.info("배경 제거 완료, 경로: {}", processedImagePath);

            String s3Url = s3UploadService.savePhoto(processedImagePath, imageUrl);
            log.info("S3 업로드 완료, S3 URL: {}", s3Url);
            return s3Url;
        } catch (Exception e) {
            log.error("이미지 처리 및 S3 업로드 중 오류 발생", e);
            throw new BusinessException(ClotheErrorCode.S3_UPLOAD_FAILED);
        }
    }

//    public String removeBackground(String imageUrl) {
//        try {
//            String downloadedImagePath = downloadImage(imageUrl);
//            log.info("Image downloaded to: {}", downloadedImagePath);
//            String processedImagePath = TEMP_DIR + extractFileName(imageUrl).replace(".jpg", ".png");
//            runPythonScript(downloadedImagePath, processedImagePath);
//            return processedImagePath;
//
//        } catch (IOException e) {
//            throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
//        } catch (Exception e) {
//            throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
//        }
//    }
    public String removeBackground(String imageUrl) {
        try {
            String downloadedImagePath = downloadImage(imageUrl);
            log.info("Image downloaded to: {}", downloadedImagePath);

            // 확장자만 추출하고 쿼리 파라미터 제거
            String cleanImageUrl = cleanUrlExtension(imageUrl);
            String processedImagePath = TEMP_DIR + extractFileName(cleanImageUrl).replace(".jpg", ".png");

            runPythonScript(downloadedImagePath, processedImagePath);
            return processedImagePath;

        } catch (IOException e) {
            throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
        } catch (Exception e) {
            throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
        }
    }
    private String cleanUrlExtension(String url) {
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));
        }
        return url;
    }

    public String downloadImage(String imagePathOrUrl) throws IOException {
        if (imagePathOrUrl == null || imagePathOrUrl.trim().isEmpty()) {
            throw new BusinessException(ClotheErrorCode.INVALID_URL_FORMAT);
        }

        BufferedImage img = null;

        if (isUrl(imagePathOrUrl)) {
            try {
                URL url = new URL(imagePathOrUrl);
                img = ImageIO.read(url);
                if (img == null) {
                    throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
                }
            } catch (IOException e) {
                throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
            }
        } else {
            File inputFile = new File(imagePathOrUrl);
            if (!inputFile.exists()) {
                throw new BusinessException(ClotheErrorCode.INVALID_URL_FORMAT);
            }
            try {
                img = ImageIO.read(inputFile);
                if (img == null) {
                    throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
                }
            } catch (IOException e) {
                throw new BusinessException(ClotheErrorCode.BACKGROUND_DELETE_FAILED);
            }
        }
        String localImagePath = TEMP_DIR + "/download" + System.currentTimeMillis() + ".png";
        File outputFile = new File(localImagePath);
        try {
            ImageIO.write(img, "png", outputFile);
        } finally {
            if (img != null) {
                img.flush();
            }
        }

        return localImagePath;
    }


    private boolean isUrl(String path) {
        try {
            new URL(path);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    private String extractFileName(String imageUrl) {
        return "processed_" + imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
    }

    private void runPythonScript(String inputImagePath, String outputImagePath) throws IOException, InterruptedException {
        String command = "C:\\Users\\lydbs\\PycharmProjects\\pythonProject\\venv\\Scripts\\python.exe " + PYTHON_SCRIPT_PATH + " " + inputImagePath + " " + outputImagePath;
        //String command = "python3 " + PYTHON_SCRIPT_PATH + " " + inputImagePath + " " + outputImagePath;

        ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
        processBuilder.redirectErrorStream(true);

        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }

        if (!process.waitFor(300, java.util.concurrent.TimeUnit.SECONDS)) {
            process.destroy();
            throw new BusinessException(ClotheErrorCode.PYTHON_SERVER_ERROR);
        }

        int exitCode = process.exitValue();

        if (exitCode != 0) {
            throw new BusinessException(ClotheErrorCode.PYTHON_SERVER_ERROR);
        }
    }
}