package com.xiaomi.mone.spider.impl;

import com.xiaomi.mone.spider.util.UrlData;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2021/11/21
 */
@Slf4j
public class Stock {

    /**
     * 股票列表
     */
    private String[] stockList = new String[]{"sh601012", "sz000977", "sz002466", "sz300750", "sz002594"};

    public List<UrlData> list(WebDriver driver) {
        List<UrlData> list = new ArrayList<>();
        for (int i = 0; i < stockList.length; i++) {
            String url = String.format("http://finance.sina.com.cn/realstock/company/%s/nc.shtml", stockList[i]);
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, 5);
            WebElement v = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id=\"price\"]")));
            UrlData d = wait.until(webDriver -> {
                String price = v.getText();
                String content = webDriver.findElement(By.xpath("//*[@id=\"stockName\"]/i")).getText();
                UrlData data = new UrlData();
                data.setName(content);
                data.setPrice(price);
                data.setUrl(url);
                data.setContent(data.getName() + ":" + data.getPrice());
                return data;
            });
            log.info("stock {}", d);
            list.add(d);
        }
        return list;
    }

}
