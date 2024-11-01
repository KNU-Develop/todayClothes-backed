package org.project.todayclothes.crawler;

import lombok.RequiredArgsConstructor;
import org.project.todayclothes.global.Category;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import static org.project.todayclothes.global.Category.*;


@Service
@RequiredArgsConstructor
public class CrawlerManagementService {
    @Value("${crawler.mode}")
    private String MODE;

    private final SieCrawlerService sieCrawlerService;
    private final KappydesinCrawlerService kappydesinCrawlerService;
    private final KreamCrawlerService kreamCrawlerService;

    @EventListener(ApplicationReadyEvent.class)
    public void executeOnceOnStartup() {
        if (MODE.equals("dev")) {
            sieCrawlerService.crawling("SIE", new Category[]{TOPS_TEE, TOPS_KNIT});
            kappydesinCrawlerService.crawling("Kappydesin", new Category[]{TOP});
            kreamCrawlerService.crawling("Kream", new Category[]{SHOES});
        }
    }


    @Scheduled(cron = "0 0 0 2 * SAT", zone = "Asia/Seoul")
    public void executePeriodically() {
        if (MODE.equals("deploy")) {
            sieCrawlerService.crawling("SIE", new Category[]{TOPS_TEE, TOPS_KNIT, TOPS_BLOUSE, PANTS, SKIRTS, OUTERS, NEW_WINTER, DRESSER, BAGS, JEWELRY});
            kappydesinCrawlerService.crawling("Kappydesin", new Category[]{TOP, OUTER, PANTS, SKIRT, ACC});
            kreamCrawlerService.crawling("Kream", new Category[]{SHOES});
        }
    }
}
