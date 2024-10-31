package org.project.todayclothes.service.crawler;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.service.ClothesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;

@Service
@RequiredArgsConstructor
public class CrawlerManagementService {
    @Value("${crawler.mode}")
    private String MODE;

//    private final ZaraCrawlerService zaraCrawlerService;
    private final SieCrawlerService sieCrawlerService;
    private final ClothesService clothesService;

//    @EventListener(ApplicationReadyEvent.class)
    public void executeOnceOnStartup() {
        if (MODE.equals("dev")) {
            sieCrawlerService.crawling();
//            zaraCrawlerService.crawlingProductHeader(driver);
        }
    }


//    @Scheduled(cron = "0 0 0 * * 3")
    public void executePeriodically() {
        if (MODE.equals("deploy")) {
            sieCrawlerService.crawling();
//            zaraCrawlerService.crawlingProductHeader(driver);
        }
    }
}
