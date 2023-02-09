/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.mone.spider.impl;

import com.beust.jcommander.internal.Lists;
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
public class CnBeta {

    public List<UrlData> list(WebDriver driver) {
        String url = "https://www.cnbeta.com/";
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, 2);
        return wait.until(webDriver->{
            log.info("cnbeta get");
            List<WebElement> elements = webDriver.findElements(By.xpath("//dl/dt/a"));
            elements.stream().forEach(it->{
                String content = it.getText();
                String u = it.getAttribute("href");
                UrlData ud = new UrlData();
                ud.setUrl(u);
                ud.setContent(content);
                log.info("{}",ud);
            });
            return Lists.newArrayList();
        });


    }

}
