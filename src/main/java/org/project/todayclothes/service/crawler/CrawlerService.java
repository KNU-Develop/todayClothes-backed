package org.project.todayclothes.service.crawler;

import org.openqa.selenium.WebDriver;
import org.project.todayclothes.global.Category;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;

public interface CrawlerService {
    @Transactional
    void crawling() throws InterruptedException, MalformedURLException;
    String getCrawlingUrl(Category category);
}
