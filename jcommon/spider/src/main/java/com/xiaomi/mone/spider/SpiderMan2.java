package com.xiaomi.mone.spider;

import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class SpiderMan2 {

    @SneakyThrows
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "/opt/program/chromedriver");
        WebDriver driver = new ChromeDriver();
        for (int i = 1; i <= 50; i++) {
            driver.get("https://www.zhihu.com/billboard");
            TimeUnit.SECONDS.sleep(2);
            ////*[@id="root"]/div/main/div/a[1]/div[2]
            ////*[@id="root"]/div/main/div/a[50]/div[2]
            WebElement element = driver.findElement(By.xpath(String.format("//*[@id=\"root\"]/div/main/div/a[%s]/div[2]",i)));
            element.click();
            System.out.println(driver.getCurrentUrl());
            TimeUnit.SECONDS.sleep(2);
            driver.navigate().back();
        }
        driver.quit();
    }
}
