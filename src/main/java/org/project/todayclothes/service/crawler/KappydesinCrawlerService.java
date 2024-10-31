package org.project.todayclothes.service.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.entity.Clothe;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.global.PRODUCT_INFO;
import org.project.todayclothes.repository.ClotheRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.project.todayclothes.global.Category.*;
import static org.project.todayclothes.global.PRODUCT_INFO.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KappydesinCrawlerService implements CrawlerService {
    private final String BASE_URL = "https://kappydesign.com";
    private final WebDriver driver;
    private final ClotheRepository clotheRepository;

    @Override
    public void crawling() {
        List<ClotheDto> clotheDtoList = new ArrayList<>();
        try {
            Category[] categories = {TOP, OUTER, PANTS, SKIRT, ACC};
            for (Category category : categories) {
                crawlingProductHead(category, clotheDtoList);
            }
            for (ClotheDto clotheDto : clotheDtoList) {
                crawlingContent(clotheDto);
                Clothe newClothe = new Clothe(clotheDto);
                log.info(newClothe.toString());
                clotheRepository.save(newClothe);
            }
        } finally {
            log.info("Kappydesin 크롤링 종료");
            driver.quit();
        }
    }
    @Async
    public void crawlingProductHead(Category category, List<ClotheDto> clotheDtoList) {
        String url = BASE_URL + getCrawlingUrl(category);
        try {
            driver.get(url);
            waitForPageLoad();
            int size = getProductCount();
            for (int i = 1; i <= size; ++i) {
                log.info(String.format("[%d/%d]", i, size));
                String name = waitForElement(getSelector(NAME, i)).getText();
                int price = Integer.parseInt(waitForElement(getSelector(PRICE, i)).getText().replaceAll("[^\\d]", ""));
                String imgUrl = waitForElement(getSelector(IMG_URL, i)).getAttribute("src");
                String link = waitForElement(getSelector(LINK, i)).getAttribute("href");
                clotheDtoList.add(ClotheDto.builder()
                        .name(name)
                        .price(price)
                        .imgUrl(imgUrl)
                        .link(link)
                        .build());
            }
        } catch (Exception e) {
            log.warn("[CRAWLING FAILED] : " + url);
        }
    }
    @Async
    public void crawlingContent(ClotheDto clotheDto) {
        if (clotheDto.getLink() == null) {
            log.warn("[CRAWLING FAILED] : crawling product link is null");
            return;
        }
        driver.get(clotheDto.getLink());
        waitForPageLoad();
        String description = waitForElement(getSelector(CONTENT)).getText();
        clotheDto.updateDescription(description);
    }
    private WebElement waitForElement(String cssSelector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
    }
    @Override
    public void waitForPageLoad() {
        waitForPageLoad(null);
    }
    @Override
    public void waitForPageLoad( String readyState) {
        if (readyState == null) {
            readyState = "complete";
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        final String finalReadyState = readyState;
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals(finalReadyState));
    }
    private int getProductCount() {
        List<WebElement> productElements = driver.findElements(
                By.cssSelector("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                        ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li")
        );
        return productElements.size();
    }
    @Override
    public String getCrawlingUrl(Category category) {
        return switch (category) {
            case TOP -> "/category/top/45";
            case OUTER -> "/category/outer/44";
            case PANTS -> "/category/pants/46";
            case SKIRT -> "/category/skirt/60";
            case ACC -> "/category/acc/48";
            default -> null;
        };
    }

    private String getSelector(PRODUCT_INFO target) {
        if (target == CONTENT) {
            return getSelector(target, -1);
        }
        return null;
    }

    @Override
    public String getSelector(PRODUCT_INFO target, int no) {
        return switch (target) {
            case NAME -> String.format("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                    ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li:nth-child(%d) > div > .description > strong >" +
                    " a > span:nth-child(2)", no);
            case PRICE -> String.format("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                    ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li:nth-child(%d) > div > .description > ul > li > " +
                    "span", no);
            case CONTENT -> "#prdDetail > div";
            case IMG_URL -> String.format("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                    ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li:nth-child(%d) > div > div > a > img", no);
            case LINK -> String.format("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                    ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li:nth-child(%d) > div > div > a", no);
        };
    }
}
