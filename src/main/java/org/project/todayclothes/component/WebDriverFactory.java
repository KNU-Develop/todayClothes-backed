package org.project.todayclothes.component;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;

@Component
public class WebDriverFactory {
    @Value("${crawler.mode}")
    private String MODE;

    @Value("${crawler.path}")
    private String chromeDriverPath;

    public WebDriver createWebDriver() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36");

        if ("dev".equals(MODE)) {
//            System.setProperty("webdriver.chrome.driver", chromeDriverPath); // 크롬드라이버 의존성 제거
            return new ChromeDriver(options);
        } else {
            return new RemoteWebDriver(new URL(chromeDriverPath), options);
        }
    }
}
