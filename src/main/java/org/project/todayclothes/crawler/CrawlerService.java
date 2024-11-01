package org.project.todayclothes.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.exception.code.CrawlingErrorCode;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.global.PRODUCT_INFO;
import org.springframework.scheduling.annotation.Async;

import java.time.Duration;

import static org.project.todayclothes.global.PRODUCT_INFO.CONTENT;

@Slf4j
public abstract class CrawlerService {
    protected static final int DEFAULT_WAIT_SEC = 10;
    @Async
    public abstract void crawling(String name, Category[] categories);

    protected void crawlingContent(WebDriver driver, ClotheDto clotheDto) {
        if (clotheDto.getLink() == null) {
            log.warn(CrawlingErrorCode.URL_CONNECT_FAIL.getMessage() + ", url is null");
            return;
        }
        try {
            connectPage(driver, clotheDto.getLink());
            String description = waitForElement(driver, getSelector(CONTENT)).getText();
            clotheDto.updateDescription(description);
        } catch (TimeoutException e) {
            log.warn(CrawlingErrorCode.CONTENT_LOAD_TIMEOUT.getMessage());
        } catch (Exception e) {
            log.warn(CrawlingErrorCode.URL_CONNECT_FAIL.getMessage());
        }
    }

    protected void connectPage(WebDriver driver, String url) throws Exception {
        try {
            driver.get(url);
            waitForPageLoad(driver);
        } catch (TimeoutException e) {
            log.warn(CrawlingErrorCode.URL_CONNECT_FAIL.getMessage() + ", url : " + url);
            throw new Exception();
        } catch (WebDriverException e) {
            log.warn(CrawlingErrorCode.CONTENT_LOAD_TIMEOUT.getMessage() + ", url : " + url);
            throw new Exception();
        }
    }
    protected WebElement waitForElement(WebDriver driver, String cssSelector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_WAIT_SEC));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(cssSelector)));
    }
    protected void waitForPageLoad(WebDriver driver) {
        waitForPageLoad(driver, null);
    }
    protected void waitForPageLoad(WebDriver driver, String readyState) {
        if (readyState == null) {
            readyState = "complete";
        }
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        final String finalReadyState = readyState;
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals(finalReadyState));
    }

    protected void logProgress(long current, long total) {
        double progress = (current / (double) total) * 100;
        System.out.printf("\rprogress...(%2.2f%%)", progress);
        if (current == total) {
            System.out.println();
        }
    }
    protected abstract String getCrawlingUrl(Category category);
    protected abstract String getSelector(PRODUCT_INFO target);
    protected abstract String getSelector(PRODUCT_INFO target, int no);

}
