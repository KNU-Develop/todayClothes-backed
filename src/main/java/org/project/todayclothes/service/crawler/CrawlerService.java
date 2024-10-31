package org.project.todayclothes.service.crawler;

import org.openqa.selenium.WebDriver;
import org.project.todayclothes.global.Category;
import org.project.todayclothes.global.PRODUCT_INFO;
import org.springframework.transaction.annotation.Transactional;

public interface CrawlerService {
    @Transactional
    void crawling();
    void waitForPageLoad();
    void waitForPageLoad(String readyState);

    String getCrawlingUrl(Category category);

    String getSelector(PRODUCT_INFO target, int no);
}
