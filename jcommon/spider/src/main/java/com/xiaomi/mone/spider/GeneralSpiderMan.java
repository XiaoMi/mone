package com.xiaomi.mone.spider;

import com.beust.jcommander.internal.Lists;
import com.xiaomi.mone.spider.impl.CnBeta;
import com.xiaomi.mone.spider.impl.OsChina;
import com.xiaomi.mone.spider.impl.Stock;
import com.xiaomi.mone.spider.util.UrlData;
import lombok.Data;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author zhangzhiyong
 * 通用爬虫(复用一个driver)
 * driver下载地址
 * https://npm.taobao.org/mirrors/chromedriver
 */
@Slf4j
@Data
public class GeneralSpiderMan implements SpiderMan {

    private boolean userRemoteDriver = true;

    private WebDriver driver;

    private ConcurrentHashMap<String, List<UrlData>> map = new ConcurrentHashMap<>();

    private List<String> list = Lists.newArrayList();

    private Object obj = new Object();

    private static final String driverPath = "/opt/program/chromedriver";

    private boolean headLess = true;

    public GeneralSpiderMan(boolean userRemoteDriver, List<String> websiteList, boolean headLess) {
        this.headLess = headLess;
        this.userRemoteDriver = userRemoteDriver;
        this.list.addAll(websiteList);
        init();
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                refresh();
            } catch (Throwable ex) {
                log.error(ex.getMessage());
            }
        }, 1, 5, TimeUnit.SECONDS);
    }

    public GeneralSpiderMan(boolean userRemoteDriver, List<String> websiteList) {
        this(userRemoteDriver, websiteList, true);
    }

    @SneakyThrows
    public void init() {
        log.info("init begin");
        this.list.forEach(it -> {
            this.map.put(it, new CopyOnWriteArrayList<>());
        });
        if (userRemoteDriver) {
            ChromeOptions cap = new ChromeOptions();
            cap.addArguments("--headless");
            cap.addArguments("--disable-gpu", "--disable-dev-shm-usage");
            driver = new RemoteWebDriver(new URL("http://127.0.0.1/"), cap);
        } else {
            System.setProperty("webdriver.chrome.driver", driverPath);
            ChromeOptions options = new ChromeOptions();
            if (headLess) {
                options.addArguments("--headless");
            }
//            options.setPageLoadTimeout(Duration.ofMillis(1000));
//            options.setImplicitWaitTimeout(Duration.ofMillis(1000));
//            options.setScriptTimeout(Duration.ofMillis(1000));
            options.setPageLoadStrategy(PageLoadStrategy.EAGER);
            driver = new ChromeDriver(options);
        }
        log.info("init end");
    }

    @SneakyThrows
    public void refresh() {
        log.info("refresh");
        IntStream.range(0, list.size()).forEach(j -> {
            String type = list.get(j);
            List<UrlData> tmp = null;
            if (type.equals("oschina")) {
                tmp = new OsChina().list(driver);
            }
            if (type.equals("stock")) {
                tmp = new Stock().list(driver);
            }
            if (type.equals("cnbeta")) {
                tmp = new CnBeta().list(driver);
            }
            final List<UrlData> udList = this.map.get(type);
            synchronized (udList) {
                udList.clear();
                udList.addAll(tmp);
            }
        });

    }


    @Override
    public CopyOnWriteArrayList<UrlData> getList(String type) {
        final List<UrlData> udList = this.map.get(type);
        synchronized (udList) {
            return new CopyOnWriteArrayList<>(udList);
        }
    }

    @Override
    public void closeDriver() {
        this.driver.quit();
    }
}
