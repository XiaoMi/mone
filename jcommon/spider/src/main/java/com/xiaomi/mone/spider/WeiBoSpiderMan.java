package com.xiaomi.mone.spider;

import com.xiaomi.mone.spider.util.UrlData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author dongzhenxing
 */
@Slf4j
public class WeiBoSpiderMan implements SpiderMan {

    private static String weiboHot = "http://s.weibo.com/top/summary?cate=realtimehot";

    private static final int RETRY_TIME = 3;

    private CopyOnWriteArrayList<UrlData> list = new CopyOnWriteArrayList<>();

    private CopyOnWriteArrayList<UrlData> viewList = new CopyOnWriteArrayList<>();

    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    private boolean userRemoteDriver = true;

    private WebDriver driver;

    public WeiBoSpiderMan(boolean userRemoteDriver) {
        this.userRemoteDriver = userRemoteDriver;
        scheduleRefreshWeboHot();
    }


    public void scheduleRefreshWeboHot() {

        try {
            System.setProperty("webdriver.chrome.driver", "/tmp/chromedriver");
            ChromeOptions options = new ChromeOptions();
            options.setHeadless(true);
            if (userRemoteDriver) {
                Capabilities cap = new ChromeOptions();
                try {
                    driver = new RemoteWebDriver(new URL("http://127.0.0.1/"), cap);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            } else {
                driver = new ChromeDriver(options);
            }
            WeiBoTask task = new WeiBoTask();
            timer.scheduleAtFixedRate(task, 1, 10, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
        }
    }


    public void refreshWebHot() throws InterruptedException {
        WebElement element;
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                driver.get(weiboHot);
                //可能需要等页面刷新
                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
                //热搜内容都在tbody中
                element = driver.findElement(By.tagName("tbody"));
            } catch (Exception e) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
                continue;
            }
            if (Objects.isNull(element)) {
                //说明没到目标页,再重试
                continue;
            }
            //每条热搜一个tr
            List<WebElement> elements = element.findElements(By.tagName("tr"));
            if (Objects.isNull(elements) || elements.isEmpty()) {
                throw new InterruptedException();
            }
            //有数据才清空
            list.clear();
            elements.forEach(event -> {
                WebElement td = event.findElement(By.className("td-02"));
                WebElement content = td.findElement(By.tagName("a"));
                log.debug("weibo get {} {}", content.getText(), content.getAttribute("href"));
                String url = content.getAttribute("href");
                if (Objects.isNull(url)) {
                    url = "";
                }
                list.add(new UrlData(content.getText(), url));
            });

            if (elements.size() > 0) {
                synchronized (this.viewList) {
                    this.viewList.clear();
                    this.viewList.addAll(list);
                }
                break;
            }
        }
    }

    private class WeiBoTask implements Runnable {

        private final SimpleDateFormat dateFormat;

        private WeiBoTask() {
            this.dateFormat = new SimpleDateFormat("HH:mm:ss");
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("weibo spider begin：" + dateFormat.format(new Date()));
            try {
                refreshWebHot();
            } catch (Throwable e) {
                log.warn("refresh weibo hot list err:", e);
            }
            log.info("weibo spider end：" + dateFormat.format(new Date()));
        }
    }

    @Override
    public CopyOnWriteArrayList<UrlData> getList(String type) {
        synchronized (this.viewList) {
            return this.viewList;
        }
    }

    @Override
    public void closeDriver() {
        try {
            driver.quit();
        } catch (Exception e) {
            log.warn("close driver err:", e);
        }
    }
}