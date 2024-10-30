package org.project.todayclothes.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class CrawlerConfig {

    @Value("${crawler.mode}")
    private String MODE;

    @Value("${crawler.path}")
    private String CHROME_DRIVER_PATH;

    @Bean
    public WebDriver webDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments(
                "user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36"
        );

        if (MODE.equals("dev")) {
            System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
            return new ChromeDriver(options);
        } else {
            return new RemoteWebDriver(new URL(CHROME_DRIVER_PATH), options);
        }
    }
}
