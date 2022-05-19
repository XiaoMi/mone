package com.xiaomi.mone.spider;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.http.HttpClient;
import org.openqa.selenium.remote.http.HttpMethod;
import org.openqa.selenium.remote.http.HttpRequest;
//import org.openqa.selenium.remote.internal.OkHttpClient;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2021/11/3
 */
public class SpiterRunner {

    public void run() throws IOException {
        Document doc = Jsoup.connect("https://kuaibao.qq.com/s/20200323A0AHBL00")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36")
                .get();
        Elements elements = doc.getElementsByClass("content-img");
        elements.forEach(it -> {
            System.out.println(it.toString());
        });
    }

    /**
     * https://npm.taobao.org/mirrors/chromedriver
     */
    @SneakyThrows
    public void run2() {
        System.setProperty("webdriver.chrome.driver", "/tmp/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("https://image.baidu.com/");

        IntStream.range(0, 20).forEach(i -> {
            ////*[@id="container"]/span[2]
            String src = driver.findElement(By.xpath("//*[@id=\"currentImg\"]")).getAttribute("src");
            System.out.println(src);
            driver.findElement(By.xpath("//*[@id=\"container\"]/span[2]")).click();
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        ////*[@id="currentImg"]
        ////*[@id="currentImg"]
//        Actions action = new Actions(driver);

//        IntStream.range(0,100).forEach(i->{
//            System.out.println("right");
//            action.sendKeys(Keys.RIGHT).perform();
//            try {
//                TimeUnit.SECONDS.sleep(2);
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
//        });


    }


    private List<String> imgList = Lists.newArrayList("//*[@id=\"mmComponent_images_1_list_1\"]/li[1]/div/div/a/div/img"
            , "//*[@id=\"mmComponent_images_1\"]/ul[1]/li[1]/div/div/a/div/img"
    );

    private WebElement getBingElement(WebDriver driver) {
        return imgList.stream().map(it -> {
            try {
                return driver.findElement(By.xpath(it));
            } catch (Throwable e) {
            }
            return null;
        }).filter(it -> null != it).findAny().get();
    }

    /**
     * 去bing搜索图片
     */
    @SneakyThrows
    public void find(String name, int num) {
        System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);
        WebDriver driver = new ChromeDriver(options);
        driver.get("https://cn.bing.com/images/trending?FORM=ILPTRD");
        TimeUnit.SECONDS.sleep(1);
        WebElement e = driver.findElement(By.id("sb_form_q"));
        e.sendKeys(name);
        e.submit();

        e = getBingElement(driver);
        e.click();
        TimeUnit.SECONDS.sleep(1);


        IntStream.range(0, num).forEach(i -> {

            WebElement iframe = null;
            try {
                iframe = driver.findElement(By.xpath("//*[@id=\"OverlayIFrame\"]"));
            } catch (Throwable ex) {

            }
            if (null != iframe) {
                WebDriver idriver = driver.switchTo().frame(iframe);
                idriver.findElement(By.xpath("//*[@id=\"navr\"]")).click();
                ;
                String src = idriver.findElement(By.xpath("//*[@id=\"mainImageWindow\"]/div[2]/div/div/div/img")).getAttribute("src");
                System.out.println(src);
//                try {
//                    putImage(cli, src, name);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
            } else {
                driver.findElement(By.xpath("//*[@id=\"navr\"]")).click();
                String src = driver.findElement(By.xpath("//*[@id=\"mainImageWindow\"]/div[2]/div/div/div/img")).getAttribute("src");
                System.out.println(src);
//                try {
//                    putImage(cli, src, name);
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
                //*[@id="mainImageWindow"]/div[2]/div/div/div/img
            }

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });


    }

    @SneakyThrows
    public void findWords2(String words, int num) {
        Capabilities cap = new ChromeOptions();
        RemoteWebDriver driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/"), cap);

//        HttpClient cli = new OkHttpClient.Factory().createClient(new URL("http://127.0.0.1:8999/"));
        try {
            int count = 0;
            int baiduPage = 1;
            int pageIndex = 1;
            for (; count < num; ) {
                WebElement pageRef = getBaiduResult(driver, words, baiduPage, pageIndex);
                if (pageRef == null) {
                    baiduPage++;
                    pageIndex = 1;
                    continue;
                }
                String ref = pageRef.getAttribute("href");
                System.out.println(ref);
                driver.get(ref);
                TimeUnit.SECONDS.sleep(1);
                List<WebElement> bts = driver.findElements(By.tagName("button"));
                for (WebElement bt : bts) {
                    if (bt.getText().contains("展开")) {
                        bt.click();
                        TimeUnit.SECONDS.sleep(1);
                        List<WebElement> cbts = driver.findElements(By.tagName("button"));
                        for (WebElement cbt : cbts) {
                            if (cbt.getText().contains("取消")) {
                                cbt.click();
                                TimeUnit.SECONDS.sleep(1);
                            }
                        }
                    }
                }
                List<WebElement> ps = driver.findElements(By.xpath("//p"));
                for (WebElement it : ps) {
                    String text = it.getText();
                    Pattern p = Pattern.compile("(\\d|[一二三四五六七八九十])([.、])([\\D].*)");
                    Matcher m = p.matcher(text);
                    if (!m.find()) {
                        continue;
                    }
                    System.out.println(StringUtils.trimWhitespace(m.group(3)));
//                putText(cli, m.group(3));
                    count++;
                    if (count >= num) {
                        break;
                    }
                }
                pageIndex++;
            }
        } catch (Exception ignored) {

        } finally {
            driver.quit();
        }
    }

    /**
     * 去百度搜正能量语句
     */
    @SneakyThrows
    public void findWords(String words, int num) {
        System.setProperty("webdriver.chrome.driver", "/opt/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(false);
        WebDriver driver = new ChromeDriver(options);

//        HttpClient cli = new OkHttpClient.Factory().createClient(new URL("http://127.0.0.1:8999/"));
        int count = 0;
        int baiduPage = 1;
        int pageIndex = 1;
        for (; count < num; ) {
            WebElement pageRef = getBaiduResult(driver, words, baiduPage, pageIndex);
            if (pageRef == null) {
                baiduPage++;
                pageIndex = 1;
                continue;
            }
            String ref = pageRef.getAttribute("href");
            System.out.println(ref);
            driver.get(ref);
            TimeUnit.SECONDS.sleep(1);
            List<WebElement> bts = driver.findElements(By.tagName("button"));
            for (WebElement bt : bts) {
                if (bt.getText().contains("展开")) {
                    bt.click();
                    TimeUnit.SECONDS.sleep(1);
                    List<WebElement> cbts = driver.findElements(By.tagName("button"));
                    for (WebElement cbt : cbts) {
                        if (cbt.getText().contains("取消")) {
                            cbt.click();
                            TimeUnit.SECONDS.sleep(1);
                        }
                    }
                }
            }
            List<WebElement> ps = driver.findElements(By.xpath("//p"));
            for (WebElement it : ps) {
                String text = it.getText();
                Pattern p = Pattern.compile("(\\d|[一二三四五六七八九十])([.、])([\\D].*)");
                Matcher m = p.matcher(text);
                if (!m.find()) {
                    continue;
                }
                System.out.println(StringUtils.trimWhitespace(m.group(3)));
//                putText(cli, m.group(3));
                count++;
                if (count >= num) {
                    break;
                }
            }
            pageIndex++;
        }
    }

    private WebElement getBaiduResult(WebDriver driver, String data, int baiduPage, int index) throws InterruptedException {
        driver.get("https://www.baidu.com");
        TimeUnit.SECONDS.sleep(1);
        driver.findElement(By.id("kw")).sendKeys(data);
        driver.findElement(By.id("kw")).sendKeys(Keys.ENTER);
        TimeUnit.SECONDS.sleep(1);
        if (baiduPage != 1) {
//            String t = driver.findElement(By.xpath("//div[@id='page']/div/a[2]")).getText();
            driver.findElement(By.xpath("//div[@id='page']/div/a[" + (baiduPage - 1) + "]")).click();
            TimeUnit.SECONDS.sleep(1);
        }
        if (StringUtils.isEmpty(driver.findElement(By.xpath("//div[@id='content_left']/div[" + index + "]")).getAttribute("class"))) {
            index++;
        }
        WebElement t = driver.findElement(By.xpath("//div[@id='content_left']/div[" + index + "]/h3/a"));
        return t;
    }

    private void putImage(HttpClient cli, String url, String tag) throws IOException {
        HttpRequest req = new HttpRequest(HttpMethod.POST, "/image");
        req.setHeader("Content-Type", "text/plain");
        req.setContent(("{\"cmd\":\"put\",\"meta\":\"" + tag + "\",\"data\":\"" + url + "\"}").getBytes());
        cli.execute(req);
    }

    private void putText(HttpClient cli, String data) throws IOException {
        HttpRequest req = new HttpRequest(HttpMethod.POST, "/text");
        req.setHeader("Content-Type", "text/plain");
        req.setContent(("{\"cmd\":\"put\",\"data\":\"" + data + "\"}").getBytes());
        cli.execute(req);
    }
}
