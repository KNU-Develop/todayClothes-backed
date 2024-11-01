package org.project.todayclothes.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.project.todayclothes.component.WebDriverFactory;
import org.project.todayclothes.dto.crawling.ClotheDto;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.global.PRODUCT_INFO;
import org.project.todayclothes.service.ClothesService;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.project.todayclothes.global.PRODUCT_INFO.*;
import static org.project.todayclothes.global.code.CrawlingSuccessCode.*;
import static org.project.todayclothes.exception.code.CrawlingErrorCode.*;

@Service
@Slf4j
public class SieCrawlerService extends CrawlerService {
    private static final String BASE_URL = "https://sie-official.kr";
    private final WebDriver driver;
    private final ClothesService clothesService;

    public SieCrawlerService(WebDriverFactory webDriverFactory, ClothesService clothesService) throws MalformedURLException {
        this.driver = webDriverFactory.createWebDriver();
        this.clothesService = clothesService;
    }
    @Override
    public void crawling(String name, Category[] categories) {
        log.info(name + START_CRAWLING.getMessage());
        List<ClotheDto> clotheDtoList = new ArrayList<>();
        try {
            log.info(START_CRAWLING_ITEM_HEADER.getMessage());
            for (Category category : categories) {
                crawlingProductHead(category, clotheDtoList);
            }
            log.info(END_CRAWLING_ITEM_HEADER.getMessage());
            log.info("크롤링 데이터(개) : "+ clotheDtoList.size());
            log.info(START_CRAWLING_ITEM_INFO.getMessage());
            int i = 1;
            int size = clotheDtoList.size();
            for (ClotheDto clotheDto : clotheDtoList) {
                logProgress(i++, size);
                crawlingContent(driver, clotheDto);
            }
            log.info(END_CRAWLING_ITEM_INFO.getMessage());
            clothesService.saveClotheDate(clotheDtoList);
        } finally {
            log.info(name + END_CRAWLING.getMessage());
            driver.quit();
        }
    }

    private void crawlingProductHead(Category category, List<ClotheDto> clotheDtoList) {
        String url = BASE_URL + getCrawlingUrl(category);
        try {
            for (int page = 1; page <= 3; ++page) {
                try {
                    connectPage(driver, url);
                } catch (Exception e) {
                    return;
                }

                int size = getProductCount();
                for (int i = 1; i <= size; ++i) {
                    logProgress(i, size);
                    String name = waitForElement(driver, getSelector(NAME, i)).getText();
                    int price = Integer.parseInt(waitForElement(driver, getSelector(PRICE, i)).getText().replace(",", ""));
                    String imgUrl = parsingImgPath(waitForElement(driver, getSelector(IMG_URL, i)).getAttribute("style"));
                    String link = waitForElement(driver, getSelector(LINK, i)).getAttribute("href");
                    clotheDtoList.add(ClotheDto.builder()
                            .name(name)
                            .price(price)
                            .imgUrl(imgUrl)
                            .link(link)
                            .category(category)
                            .build());
                }
                if (size == 0) {
                    break;
                }
                url = getNextPageUrl(url, page);
            }
        } catch (TimeoutException  e) {
            log.warn(CONTENT_LOAD_TIMEOUT.getMessage());
        }
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

    private String getNextPageUrl(String url, int page) {
        return String.format("%s&page=%d", url, page);
    }

    @Override
    protected String getCrawlingUrl(Category category) {
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
    @Override
    protected String getSelector(PRODUCT_INFO target) {
        return switch (target) {
            case CONTENT, PRODUCT_LIST -> getSelector(target, -1);
            default -> null;
        };
    }
    @Override
    protected String getSelector(PRODUCT_INFO target, int no) {
        return switch (target) {
            case NAME -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__text-wrapper" +
                    " > a > span", no);
            case PRICE -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__text-wrapper > div > span.display > span.original-price", no);
            case CONTENT -> "#productInfo > div.product-single__offer > div > div.xans-element-.xans-product.xans-product-detail.product-single__header > div.product-single__simple-desc";
            case PRODUCT_LIST -> "#wrap > div > div.page-full-width > div > ul > li";
            case IMG_URL -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__image-wrapper > a > " +
                    "div > div", no);
            case LINK -> String.format("#wrap > div > div.page-full-width > div > ul > li:nth-child(%d) > div.product-item__image-wrapper > a",
                    no);
            default -> null;
        };
    }
}
