package org.project.todayclothes.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.todayclothes.component.WebDriverFactory;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.global.PRODUCT_INFO;
import org.project.todayclothes.service.ClothesService;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.project.todayclothes.global.PRODUCT_INFO.*;
import static org.project.todayclothes.global.code.CrawlingSuccessCode.*;
import static org.project.todayclothes.exception.code.CrawlingErrorCode.*;

@Service
@Slf4j
public class KappydesinCrawlerService extends CrawlerService {
    private static final String BASE_URL = "https://kappydesign.com";
    private final WebDriverFactory webDriverFactory;
    private final ClothesService clothesService;

    public KappydesinCrawlerService(WebDriverFactory webDriverFactory, ClothesService clothesService) {
        this.webDriverFactory = webDriverFactory;
        this.clothesService = clothesService;
    }


    @Override
    public void crawling(String name, Category[] categories) {
        log.info(name + START_CRAWLING.getMessage());
        List<ClotheDto> clotheDtoList = new ArrayList<>();
        Set<String> processedUrls = new HashSet<>();

        WebDriver driver = null; // WebDriver를 여기서 선언
        try {
            driver = webDriverFactory.createWebDriver();
            log.info(START_CRAWLING_ITEM_HEADER.getMessage());

            for (Category category : categories) {
                crawlingProductHead(driver, category, clotheDtoList, processedUrls);
            }

            log.info(END_CRAWLING_ITEM_HEADER.getMessage());
            log.info("크롤링 데이터(개) : " + clotheDtoList.size());
            log.info(START_CRAWLING_ITEM_INFO.getMessage());

            int i = 1;
            int size = clotheDtoList.size();
            for (ClotheDto clotheDto : clotheDtoList) {
                logProgress(i++, size);
                crawlingContent(driver, clotheDto);
            }

            log.info(END_CRAWLING_ITEM_INFO.getMessage());
            clothesService.saveClotheDate(clotheDtoList);
        } catch (Exception e) {
            log.error("크롤링 중 오류 발생", e);
        } finally {
            log.info(name + END_CRAWLING.getMessage());
            if (driver != null) {
                driver.quit(); // WebDriver 자원 해제
            }
        }
    }


    private void crawlingProductHead(WebDriver driver, Category category, List<ClotheDto> clotheDtoList, Set<String> processedUrls) {
        String url = BASE_URL + getCrawlingUrl(category);
        try {
            connectPage(driver, url);
        } catch (Exception e) {
            log.error("페이지 접속 실패: " + url, e);
            return;
        }
        try {
            int size = getProductCount(driver);
            for (int i = 1; i <= size; ++i) {
                logProgress(i, size);
                String name = waitForElement(driver, getSelector(NAME, i)).getText();
                int price = Integer.parseInt(waitForElement(driver, getSelector(PRICE, i)).getText().replaceAll("[^\\d]", ""));
                String imgUrl = waitForElement(driver, getSelector(IMG_URL, i)).getAttribute("src");
                String image = clothesService.generateS3Url("kappy", imgUrl);
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
            if (size == 0) {
                log.warn(ELEMENT_NOT_FOUND.getMessage());
            }
        } catch (TimeoutException e) {
            log.warn(CONTENT_LOAD_TIMEOUT.getMessage());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private int getProductCount(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        AtomicInteger previousProductCount = new AtomicInteger(driver.findElements(By.cssSelector(getSelector(PRODUCT_LIST))).size());
        AtomicInteger curProductCount = new AtomicInteger(previousProductCount.get());
        log.info(CHECKING_ITEM_LIST.getMessage());
        int maxPage = 20;
        int curPage = 1;

        while (curPage <= maxPage) {
            ++curPage;
            System.out.printf("\r현재 확인한 제품 개수 : %d", curProductCount.get());
            try {
                WebElement moreButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.cssSelector(getSelector(BUTTON))));
                moreButton.click();
                wait.until(d -> {
                    curProductCount.set(d.findElements(By.cssSelector(getSelector(PRODUCT_LIST))).size());
                    return curProductCount.get() > previousProductCount.get();
                });
                previousProductCount.set(curProductCount.get());
            } catch (TimeoutException e) {
                log.warn(ELEMENT_NOT_FOUND_FIND_BREAK.getMessage());
                break;
            } catch (NoSuchElementException e) {
                log.warn(ELEMENT_NOT_FOUND.getMessage());
                break;
            }
        }
        System.out.println();
        if (curPage > maxPage) {
            log.warn(INFINITE_FIND_BREAK.getMessage());
        }
        return previousProductCount.get();
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

    public String getSelector(PRODUCT_INFO target) {
        return switch (target) {
            case CONTENT, BUTTON, PRODUCT_LIST-> getSelector(target, -1);
            default -> null;
        };
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
            case PRODUCT_LIST -> "#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div.xans-element-" +
                    ".xans-product.xans-product-listnormal.ec-base-product > ul > li";
            case BUTTON -> "#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div.xans-element-" +
                    ".xans-product.xans-product-listmore.more > a";
            case IMG_URL -> String.format("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                    ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li:nth-child(%d) > div > div > a > img", no);
            case LINK -> String.format("#contents > div.xans-element-.xans-product.xans-product-normalpackage > div.product_area > div" +
                    ".xans-element-.xans-product.xans-product-listnormal.ec-base-product > ul > li:nth-child(%d) > div > div > a", no);
        };
    }
}
