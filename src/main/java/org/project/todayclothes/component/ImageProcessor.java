package org.project.todayclothes.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.project.todayclothes.dto.ClotheRecommendDto;
import org.project.todayclothes.dto.ResRecommendClotheDto;
import org.project.todayclothes.dto.gpt.GptResClotheRecommendDto;
import org.project.todayclothes.entity.Clothe;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ClotheErrorCode;
import org.project.todayclothes.repository.ClotheRepository;
import org.project.todayclothes.service.ClothesService;
import org.project.todayclothes.service.S3ImageService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageProcessor {
    final int WRAPPER_WIDTH = 2000;
    final int WRAPPER_HEIGHT = 2000;
    final int IMAGE_WIDTH = (int) (WRAPPER_WIDTH * 0.4);
    final int PADDING_WIDTH = (int) (WRAPPER_WIDTH * 0.125);
    final int PADDING_HEIGHT = (int) (WRAPPER_HEIGHT * 0.125);
    final int[][] positions = {
            {(int) (WRAPPER_WIDTH * 0.40), PADDING_HEIGHT},                 // TOP
            {PADDING_WIDTH, WRAPPER_HEIGHT / 2},                            // BOTTOM
            {WRAPPER_WIDTH / 2, WRAPPER_HEIGHT / 2},                        // SHOES
            {PADDING_WIDTH, PADDING_HEIGHT},                                // ACC 1
            {IMAGE_WIDTH + PADDING_WIDTH, PADDING_HEIGHT},                  // ACC 2
    };
    final String PACKAGE_NAME = "recommendImage";
    private final S3ImageService s3ImageService;
    private final ClotheRepository clotheRepository;

    public String getRecommendImageUrl(ResRecommendClotheDto clotheDto) throws Exception {
        log.debug("AI에서 받은 응답: {}", clotheDto);
        List<String> imagePaths = getRecommendItemUrlList(clotheDto);

        BufferedImage finalImage = createRecommendImage(imagePaths);

        String filaName = PACKAGE_NAME + UUID.randomUUID();
//        ImageIO.write(finalImage, "png", new File("src/main/resources/static/merged_image.png"));
        return s3ImageService.uploadImage(finalImage, filaName);
    }
    private List<String> getRecommendItemUrlList(ResRecommendClotheDto clotheDto) {
        List<String> imagePaths;
        try {
            String acc2;
            if (clotheDto.getOuter() == null) {
                acc2 = clotheRepository.findById(clotheDto.getAcc1()).get().getImage();
            } else {
                acc2 = clotheRepository.findById(clotheDto.getAcc2()).get().getImage();
            }

            imagePaths = List.of(
                    clotheRepository.findById(clotheDto.getTop()).get().getImage(),
                    clotheRepository.findById(clotheDto.getBottom()).get().getImage(),
                    clotheRepository.findById(clotheDto.getShoes()).get().getImage(),
                    clotheRepository.findById(clotheDto.getAcc1()).get().getImage(),
                    acc2
            );
            log.debug("Fetched image paths: {}", imagePaths);
        } catch (Exception e) {
            log.error("Failed to fetch clothing items for clotheDto: {}", clotheDto, e);
            throw new BusinessException(ClotheErrorCode.CLOTHES_NOT_FOUND);
        }
        return imagePaths;
    }


    private BufferedImage createRecommendImage(List<String> imagePaths) throws IOException {
        BufferedImage canvas = new BufferedImage(WRAPPER_WIDTH, WRAPPER_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = canvas.createGraphics();

        final int size = imagePaths.size();
        for (int i = 0; i < size; i++) {
            BufferedImage originalImage = ImageIO.read(new URL(imagePaths.get(i)));
            int height = (int) ((double) originalImage.getHeight() / originalImage.getWidth() * IMAGE_WIDTH);
            BufferedImage resizedImage = new BufferedImage(IMAGE_WIDTH, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D resizeG = resizedImage.createGraphics();
            resizeG.drawImage(originalImage, 0, 0, IMAGE_WIDTH, height, null);
            resizeG.dispose();
            g.drawImage(resizedImage, positions[i][0], positions[i][1], null);
        }

        g.dispose();
        return canvas;
    }
}
