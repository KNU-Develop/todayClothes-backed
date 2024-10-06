package org.project.todayclothes.service.crawler;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.global.Category;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.project.todayclothes.global.Category.*;

@Service
@EnableScheduling
public class ZaraCrawlerService {
    private final String DEV_CHROME_DRIVER_PATH = "C:\\Program Files\\chromedriver-win64\\chromedriver.exe";

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


//    @Scheduled(fixedRate = 100000)
    @EventListener(ApplicationReadyEvent.class)
    public void crawlingProductHeader() {
        System.setProperty("webdriver.chrome.driver", DEV_CHROME_DRIVER_PATH);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36");
        WebDriver driver = new ChromeDriver(options);

        List<ClotheDto> clotheDtoList = new ArrayList<>();
        crawlingByCategory(TOP, driver, clotheDtoList);
        crawlingByCategory(PANTS, driver, clotheDtoList);
        crawlingByCategory(CAP, driver, clotheDtoList);
//        crawlingByCategory(ACCESSORIES, driver, clotheDtoList);
        crawlingByCategory(SHOES, driver, clotheDtoList);
        crawlingByCategory(OUTER, driver, clotheDtoList);


        for (ClotheDto s : clotheDtoList) {
            System.out.println(s.toString());
        }

        driver.quit();
    }

    private WebElement waitForElement(WebDriver driver, String cssSelector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
    }

    private void crawlingByCategory(Category category, WebDriver driver, List<ClotheDto> clotheDtoList) {
        String categoryUrl = getCategoryUrl(category);
        if (categoryUrl == null) return;

        for (int page = 1; page <= 1; ++page) {
            driver.get(BASE_URL + categoryUrl + page);
            int pageSize = (page - 1) * 80;

            for (int i = 1; i <= 80; ++i) {
                try {
                    String DYNAMIC_PRODUCT_WRAPPER = PRODUCT_WRAPPER + (pageSize + i);

                    String name = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_NAME).getText();
                    String priceText = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_PRICE).getText();
                    int price = Integer.parseInt(priceText.replaceAll("[^0-9]", ""));
                    String link = waitForElement(driver, DYNAMIC_PRODUCT_WRAPPER + PRODUCT_LINK).getAttribute("href");

                    clotheDtoList.add(ClotheDto.builder()
                            .name(name)
                            .price(price)
                            .category(category)
                            .content(null)
                            .link(link)
                            .imgUrl(null)
                            .build());
                    System.out.println(i);
                } catch (Exception e) {
                    break;
                }
            }
        }

    }

    private String getCategoryUrl(Category category) {
        return switch (category) {
            case TOP -> "/man-sale-l7139.html?v1=2444848&page=";
            case PANTS -> "/man-sale-l7139.html?v1=2444333&page=";
//            case ACCESSORIES -> "/man-accessories-l537.html?v1=2436444&page=";
            case CAP -> "/man-accessories-hats-caps-l546.html?v1=2436437&page=";
            case SHOES -> "/man-sale-l7139.html?v1=2443851&page=";
            case OUTER -> "/man-sale-l7139.html?v1=2444334&page=";
            default -> null;
        };
    }
}
