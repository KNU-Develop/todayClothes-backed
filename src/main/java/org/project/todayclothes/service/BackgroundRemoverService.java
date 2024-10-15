package org.project.todayclothes.service;

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

@Slf4j
@Service
public class BackgroundRemoverService {

    //private static final String PYTHON_SCRIPT_PATH = "C:\\Users\\lydbs\\PycharmProjects\\pythonProject\\remove_bg.py";
    private static final String PYTHON_SCRIPT_PATH = "/app/scripts/remove_bg.py";
    private static final String TEMP_DIR = "/app/temp/img/";

    public String removeBackground(String imageUrl) {
        try {
            String downloadedImagePath = downloadImage(imageUrl);
            log.info("Image downloaded to: {}", downloadedImagePath);
            String processedImagePath = TEMP_DIR + "/processed_" + System.currentTimeMillis() + ".png";
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

    private void runPythonScript(String inputImagePath, String outputImagePath) throws IOException, InterruptedException {
        //String command = "C:\\Users\\lydbs\\PycharmProjects\\pythonProject\\venv\\Scripts\\python.exe " + PYTHON_SCRIPT_PATH + " " + inputImagePath + " " + outputImagePath;
        String command = "python3 " + PYTHON_SCRIPT_PATH + " " + inputImagePath + " " + outputImagePath;

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