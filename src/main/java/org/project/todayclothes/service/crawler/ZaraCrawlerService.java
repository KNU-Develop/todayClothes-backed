package org.project.todayclothes.service.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.entity.Clothe;
import org.project.todayclothes.exception.BusinessException;
import org.project.todayclothes.exception.code.ClotheErrorCode;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.repository.ClotheRepository;
import org.springframework.beans.factory.annotation.Value;
import org.project.todayclothes.service.BackgroundRemoverService;
import org.project.todayclothes.service.ClothesService;
import org.project.todayclothes.service.S3UploadService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.project.todayclothes.global.Category.*;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class ZaraCrawlerService {
    // BASE URL
    private final String BASE_URL = "https://www.zara.com/kr/ko";

    private final String PANTS_PRODUCT_URL = "/man-sale-l7139.html?v1=2444333&page=";
    private final String ACCESSORIES_PRODUCT_URL = "/man-accessories-l537.html?v1=2436444&page=";
    private final String SHOSE_PRODUCT_URL = "/man-shoes-l769.html?v1=2436382&page=";
    private final String OUTER_PRODUCT_URL = "/man-sale-l7139.html?v1=2444334&page=";
    // SELECTOR
    private final String PRODUCT_WRAPPER = "#main > article > div.product-groups > section.product-grid.product-grid--reticle" +
            ".product-grid--is-zoom2 > ul > li:nth-child(";
    private final String PRODUCT_NAME = ") > div.product-grid-product__data > div > div > div > div.product-grid-product-info__main-info > a > h2";
    private final String PRODUCT_PRICE = ") > div.product-grid-product__data > div > div > div > div.product-grid-product-info__product-price.price" +
            " > span > span > span > div > span";
    private final String PRODUCT_LINK = ") > div.product-grid-product__data > div > div > div > div.product-grid-product-info__main-info > a";
    private final String PRODUCT_IMG_URL = ") > div.product-grid-product__figure > a > div > div > div > img";


    private final ClotheRepository clotheRepository;
    private final ClothesService clothesService;

    @Transactional
    public void crawlingProductHeader(WebDriver driver) throws InterruptedException {
        List<ClotheDto> clotheDtoList = new ArrayList<>();
        try {
            crawlingByCategory(TOP, driver, clotheDtoList);
            crawlingByCategory(PANTS, driver, clotheDtoList);
            crawlingByCategory(BEANIE, driver, clotheDtoList);
            crawlingByCategory(CAP, driver, clotheDtoList);
            crawlingByCategory(SUNGLASSES, driver, clotheDtoList);
            crawlingByCategory(SHOES, driver, clotheDtoList);
            crawlingByCategory(OUTER, driver, clotheDtoList);

            for (ClotheDto clotheDto : clotheDtoList) {
                // 이미지 URL로 기존 데이터 확인
                boolean alreadyExists = clotheRepository.existsByImgUrl(clotheDto.getImgUrl());

                if (alreadyExists) {
                    Clothe existingClothe = clotheRepository.findByImgUrl(clotheDto.getImgUrl())
                            .orElseThrow(() -> new BusinessException(ClotheErrorCode.CLOTHES_NOT_FOUND));

                    if (existingClothe.getImage() != null) {
                        log.info("이미 처리가 완료된 데이터입니다. imgUrl: {}", existingClothe.getImgUrl());
                        continue;
                    }
                    try {
                        clothesService.processAndUploadImage(existingClothe);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }

                Clothe newClothe = new Clothe(clotheDto);
                clotheRepository.save(newClothe);

                try {
                    clothesService.processAndUploadImage(newClothe);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            driver.quit(); // 전체 크롤링이 끝난 후 드라이버 종료
        }
    }

    private WebElement waitForElement(WebDriver driver, String cssSelector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
    }

    @Async
    public void crawlingByCategory(Category category, WebDriver driver, List<ClotheDto> clotheDtoList) throws InterruptedException {
        String categoryUrl = getCategoryUrl(category);
        if (categoryUrl == null) return;

        for (int page = 1; page <= 1; ++page) {
            driver.get(BASE_URL + categoryUrl + page);
            try {
                Thread.sleep(1000);
                JavascriptExecutor js = (JavascriptExecutor) driver;
                long currentScrollPosition = 0;
                long scrollHeight = (long) js.executeScript("return document.body.scrollHeight");
                while (currentScrollPosition < scrollHeight) {
                    js.executeScript("window.scrollBy(0, 200);");
                    currentScrollPosition = (long) js.executeScript("return window.pageYOffset;");
                    Thread.sleep(300);
                    scrollHeight = (long) js.executeScript("return document.body.scrollHeight");
                }
            } catch (Exception e) {
            }
            int pageSize = (page - 1) * 80;

            for (int i = 1; i <= 80; ++i) {
                try {
                    String DYNAMIC_PRODUCT_WRAPPER = PRODUCT_WRAPPER + (pageSize + i);

                    String name = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_NAME).getText();
                    String priceText = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_PRICE).getText();
                    int price = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
                    String link = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_LINK).getAttribute("href");
                    String imgUrl = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_IMG_URL).getAttribute("src");
                    clotheDtoList.add(ClotheDto.builder()
                            .name(name)
                            .price(price)
                            .category(category == BEANIE ? CAP : category)
                            .description(null)
                            .link(link)
                            .imgUrl(imgUrl)
                            .build());
                } catch (Exception e) {
                    break;
                }
            }
            driver.quit();
        }

    }

    private String getCategoryUrl(Category category) {
        return switch (category) {
            case TOP -> "/man-sale-l7139.html?v1=2444848&page=";
            case PANTS -> "/man-sale-l7139.html?v1=2444333&page=";
            case BEANIE -> "/man-accesorios-beanie-l5142.html?v1=2436447&page=";
            case CAP -> "/man-accessories-hats-caps-l546.html?v1=2436437&page=";
            case SUNGLASSES -> "/man-accessories-sunglasses-l558.html?v1=2436450&page=";
            case SHOES -> "/man-sale-l7139.html?v1=2443851&page=";
            case OUTER -> "/man-sale-l7139.html?v1=2444334&page=";
            default -> null;
        };
    }
}
