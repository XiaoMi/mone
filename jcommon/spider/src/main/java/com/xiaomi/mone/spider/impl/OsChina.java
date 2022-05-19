package com.xiaomi.mone.spider.impl;

import com.xiaomi.mone.spider.util.UrlData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2021/11/21
 */
@Slf4j
public class OsChina {

    public List<UrlData> list(WebDriver driver) {
        String url = "https://www.oschina.net";
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 2);
        List<UrlData> news = wait.until(webDriver -> IntStream.range(1, 3).mapToObj(i -> {
            List<WebElement> elements = driver.findElements(By.xpath(String.format("//*[@id=\"v_news\"]/div/div[%s]/div[3]/div/div[*]/a", i)));
            return elements.stream().map(it -> {
                UrlData data = new UrlData();
                data.setUrl(it.getAttribute("href"));
                data.setContent(it.getAttribute("title"));
                log.info("osChina:{}", data);
                return data;
            }).collect(Collectors.toList());
        }).flatMap(it -> it.stream()).collect(Collectors.toList()));
        return news;
    }

}
