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
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.project.todayclothes.global.Category.*;
import static org.project.todayclothes.global.PRODUCT_INFO.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class SieCrawlerService implements CrawlerService {
    private final String BASE_URL = "https://sie-official.kr";
    private final WebDriver driver;
    private final ClotheRepository clotheRepository;
    @Transactional
    public void crawling() {
        List<ClotheDto> clotheDtoList = new ArrayList<>();
        try {
            Category[] categories = {TOPS_TEE, TOPS_KNIT, TOPS_BLOUSE, PANTS, SKIRTS, OUTERS, NEW_WINTER, DRESSER, BAGS, JEWELRY};
            for (Category category : categories) {
                crawlingProductHead(category, driver, clotheDtoList);
            }
            for (ClotheDto clotheDto : clotheDtoList) {
                crawlingContent(driver, clotheDto);
                Clothe newClothe = new Clothe(clotheDto);
                clotheRepository.save(newClothe);
            }
        } finally {
            log.info("SIE 크롤링 종료");
            driver.quit();
        }
    }

    @Async
    public void crawlingProductHead(Category category, WebDriver driver, List<ClotheDto> clotheDtoList) {
        String url = BASE_URL + getCrawlingUrl(category);
        try {
            for (int page = 1; page <= 2; ++page) {
                try {
                    driver.get(url);
                    waitForPageLoad(driver, "complete");
                } catch (Exception e) {
                    log.warn("[CRAWLING FAILED] fail load page : " + url);
                }

                int size = 0;
                try {
                    size = getProductCount(driver);
                } catch (Exception e) {
                    log.warn("[CRAWLING FAILED] fail check page size : " + url);
                }
                for (int i = 1; i <= size; ++i) {
                    String name = waitForElement(driver, getSelector(NAME, i)).getText();
                    int price = Integer.parseInt(waitForElement(driver, getSelector(PRICE, i)).getText().replace(",", ""));
                    String imgUrl = parsingImgPath(waitForElement(driver, getSelector(IMG_URL, i)).getAttribute("style"));
                    String link = waitForElement(driver, getSelector(LINK, i)).getAttribute("href");
                    clotheDtoList.add(ClotheDto.builder()
                            .name(name)
                            .price(price)
                            .imgUrl(imgUrl)
                            .link(link)
                            .build());
                }
                url = getNextPageUrl(url, page);
            }
        } catch (Exception e) {
            log.warn("[CRAWLING FAILED] : " + url);
        }
    }
    @Async
    public void crawlingContent(WebDriver driver, ClotheDto clotheDto) {
        if (clotheDto.getLink() == null) {
            log.warn("[CRAWLING FAILED] : crawling product link is null");
            return;
        }
        driver.get(clotheDto.getLink());
        waitForPageLoad(driver);
        String description = waitForElement(driver, getSelector(CONTENT)).getText();
        clotheDto.updateDescription(description);
        log.info("crawling -> "+clotheDto);
    }
    private String parsingImgPath(String src) {
        Pattern pattern = Pattern.compile("url\\((['\"]?)(//[^'\"]+)\\1\\)");
        Matcher matcher = pattern.matcher(src);

        if (matcher.find()) {
            String url = matcher.group(2);

            if (!url.startsWith("http")) {
                url = "https:" + url;
            }
            return url;
        }
        return null;
    }
    private WebElement waitForElement(WebDriver driver, String cssSelector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
    }

    private void waitForPageLoad(WebDriver driver) {
        waitForPageLoad(driver, null);
    }
    private void waitForPageLoad(WebDriver driver, String readyState) {
        if (readyState == null) {
            readyState = "complete";
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        final String finalReadyState = readyState;
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals(finalReadyState));
    }

    private int getProductCount(WebDriver driver) {
        List<WebElement> productElements = driver.findElements(
                By.cssSelector("#wrap > div > div.page-full-width > div > ul > li")
        );
        return productElements.size();
    }

    private String getNextPageUrl(String url, int page) {
        return String.format("%s&page=%d", url, page);
    }

    public String getCrawlingUrl(Category category) {
        return switch (category) {
            case TOPS_TEE -> "/product/list.html?cate_no=78";
            case TOPS_KNIT -> "/product/list.html?cate_no=79";
            case TOPS_BLOUSE -> "/product/list.html?cate_no=80";
            case PANTS -> "/product/list.html?cate_no=58";
            case SKIRTS -> "/product/list.html?cate_no=84";
            case OUTERS -> "/product/list.html?cate_no=55";
            case NEW_WINTER -> "/product/list.html?cate_no=665";
            case DRESSER -> "/product/list.html?cate_no=57";
            case BAGS -> "/product/list.html?cate_no=266";
            case JEWELRY -> "/product/list.html?cate_no=86";
            default -> null;
        };
    }
    private String getSelector(PRODUCT_INFO target) {
        if (target == CONTENT) {
            return getSelector(target, -1);
        }
        return null;
    }

    private String getSelector(PRODUCT_INFO target, int no) {
        return switch (target) {
            case NAME -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__text-wrapper" +
                    " > a > span", no);
            case PRICE -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__text-wrapper > div > span.display > span.original-price", no);
            case CONTENT -> "#productInfo > div.product-single__offer > div > div.xans-element-.xans-product.xans-product-detail.product-single__header > div.product-single__simple-desc";
            case IMG_URL -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__image-wrapper > a > " +
                    "div > div", no);
            case LINK -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__image-wrapper > a",
                    no);
        };
    }
}
