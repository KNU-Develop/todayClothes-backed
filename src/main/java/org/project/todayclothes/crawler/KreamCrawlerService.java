package org.project.todayclothes.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.project.todayclothes.component.WebDriverFactory;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.global.PRODUCT_INFO;
import org.project.todayclothes.service.BackgroundRemoverService;
import org.project.todayclothes.service.ClothesService;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.project.todayclothes.exception.code.CrawlingErrorCode.*;
import static org.project.todayclothes.global.Category.*;
import static org.project.todayclothes.global.PRODUCT_INFO.*;
import static org.project.todayclothes.global.code.CrawlingSuccessCode.*;


@Slf4j
@Service
public class KreamCrawlerService extends CrawlerService {
    private static final String BASE_URL = "https://kream.co.kr";
    private final WebDriver driver;
    private final ClothesService clothesService;

    public KreamCrawlerService(WebDriverFactory webDriverFactory, ClothesService clothesService ) throws MalformedURLException {
        this.driver = webDriverFactory.createWebDriver();
        this.clothesService = clothesService;
    }

    @Override
    public void crawling(String name, Category[] categories) {
        log.info(name + START_CRAWLING.getMessage());
        List<ClotheDto> clotheDtoList = new ArrayList<>();
        Set<String> processedUrls = new HashSet<>();
        try {
            log.info(START_CRAWLING_ITEM_HEADER.getMessage());
            for (Category category : categories) {
                crawlingProductHead(category, clotheDtoList, processedUrls);
            }
            log.info(END_CRAWLING_ITEM_HEADER.getMessage());
            log.info("크롤링 데이터(개) : "+ clotheDtoList.size());
            clothesService.saveClotheDate(clotheDtoList);
        } finally {
            log.info(name + END_CRAWLING.getMessage());
            driver.quit();
        }
    }
    private void crawlingProductHead(Category category, List<ClotheDto> clotheDtoList, Set<String> processedUrls) {
        String url = BASE_URL + getCrawlingUrl(category);
        try {
            try {
                connectPage(driver, url);
                scrollToPageEnd();
            } catch (Exception e) {
                System.out.println("ERROR!");
                return;
            }

            int size = getProductCount();
            System.out.println(size);
            for (int i = 1; i <= size; ++i) {
                logProgress(i, size);
                String name = waitForElement(driver, getSelector(NAME, i)).getText();
                int price = Integer.parseInt(waitForElement(driver, getSelector(PRICE, i)).getText().replaceAll("[^0-9]", ""));
                String imgUrl = waitForElement(driver, getSelector(IMG_URL, i)).getAttribute("src");
                String image = clothesService.generateS3Url("kream", imgUrl);
                String link = waitForElement(driver, getSelector(LINK, i)).getAttribute("href");
                if (processedUrls.contains(imgUrl)) {
                    log.warn("중복된 이미지 URL: " + imgUrl);
                    continue;
                }
                processedUrls.add(imgUrl);
                clotheDtoList.add(ClotheDto.builder()
                        .name(name)
                        .price(price)
                        .imgUrl(imgUrl)
                        .image(image)
                        .link(link)
                        .category(category)
                        .build());
            }
        } catch (TimeoutException e) {
            log.warn(CONTENT_LOAD_TIMEOUT.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    private void scrollToPageEnd() throws InterruptedException {
        long maxHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
        long curHeight = 0;
        while (curHeight < maxHeight - 1000) {
            ((JavascriptExecutor) driver).executeScript("window.scrollBy(0, 200);");

            Thread.sleep(300);
            curHeight = (long) ((JavascriptExecutor) driver).executeScript("return window.pageYOffset");
            maxHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.body.scrollHeight");
            logProgress(curHeight, maxHeight);
        }
    }
    private int getProductCount() {
        try {
            List<WebElement> productElements = driver.findElements(
                    By.cssSelector(getSelector(PRODUCT_LIST))
            );
            return productElements.size();
        } catch (TimeoutException e) {
            log.warn(ELEMENT_NOT_FOUND_FIND_BREAK.getMessage());
            return 0;
        }
    }

    @Override
    protected String getCrawlingUrl(Category category) {
        if (category == SHOES) return "/exhibitions/756";
        return null;
    }

    @Override
    protected String getSelector(PRODUCT_INFO target) {
        if (target == PRODUCT_LIST) {
            return getSelector(target, -1);
        }
        return null;
    }

    @Override
    protected String getSelector(PRODUCT_INFO target, int no) {
        return switch (target) {
            case NAME -> String.format("#ex0 > div.exhibition_item_products.exhibition_item_section.product_list > div:nth-child(%d) > a > div" +
                    ".product_info_area > div.title > div > p.name", no);
            case PRICE -> String.format("#ex0 > div.exhibition_item_products.exhibition_item_section.product_list > div:nth-child(%d) > a > div" +
                    ".price.price_area > p.amount", no);
            case PRODUCT_LIST -> "#ex0 > div.exhibition_item_products.exhibition_item_section.product_list > div";
            case IMG_URL -> String.format("#ex0 > div.exhibition_item_products.exhibition_item_section.product_list > div:nth-child(%d) > a > div" +
                    ".product > picture > img", no);
            case LINK -> String.format("#ex0 > div.exhibition_item_products.exhibition_item_section.product_list > div:nth-child(%d) > a", no);
            default -> null;
        };
    }
}
