package com.xiaomi.mone.spider.test;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/7/19 14:01
 */
public class BingTest {

    private List<String> imgList = Lists.newArrayList("//*[@id=\"mmComponent_images_1_list_1\"]/li[1]/div/div/a/div/img"
            , "//*[@id=\"mmComponent_images_1\"]/ul[1]/li[1]/div/div/a/div/img"
    );


    private String path = "/Users/zhangzhiyong/program/fenlei/";

    /**
     * 用来从bing下载机器学习分类的图片
     */
    @SneakyThrows
    @Test
    public void testBing() {
        Map<String, String> m = new HashMap<>();
//        m.put("澜 王者荣耀", "lan");
//        m.put("露娜 王者荣耀", "luna");
//        m.put("诸葛亮 王者荣耀", "zhugeliang");
//        m.put("马超 王者荣耀", "machao");
//        m.put("王昭君 王者荣耀", "wangzhaojun");
//        m.put("关羽 王者荣耀", "guanyu");
//        m.put("花木兰 王者荣耀", "huamulan");
//        m.put("妲己 王者荣耀", "daji");
//        m.put("西施 王者荣耀", "xishi");
//        m.put("武则天 王者荣耀", "wuzetian");
//        m.put("钟无艳 王者荣耀", "zhongwuyan");
//        m.put("甄姬 王者荣耀", "zhenji");
//        m.put("小乔 王者荣耀", "小乔");
//        m.put("东皇太一 王者荣耀", "donghuangtaiyi");
//        m.put("孙尚香 王者荣耀", "sunshangxiang");
//        m.put("李元芳 王者荣耀", "liyuanfang");
//        m.put("金蝉 王者荣耀", "jinchan");
//        m.put("刘备 王者荣耀", "liubei");
//        m.put("鲁班 王者荣耀", "luban");
//        m.put("安其拉 王者荣耀", "anqila");
//        m.put("庄周 王者荣耀", "dayu");
//        m.put("曹操 王者荣耀", "caocao");
//        m.put("伽罗 王者荣耀", "jialun");
//        m.put("李白 王者荣耀", "libai");
//        m.put("瑶 王者荣耀", "yao");

        m.entrySet().forEach(it -> {
            find(it.getKey(), 50, it.getValue());
            try {
                TimeUnit.SECONDS.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        Thread.currentThread().join();
    }

    @SneakyThrows
    public static void download(String urlString, String filename) {
        try {
            System.out.println("download:" + filename);
            URL url = new URL(urlString); // 构造URL
            URLConnection con = url.openConnection();  // 打开链接
            con.setConnectTimeout(5 * 1000);  //设置请求超时为5s
            InputStream is = con.getInputStream();  // 输入流
            byte[] bs = new byte[1024];  // 1K的数据缓冲
            int len;  // 读取到的数据长度
            int i = filename.length();
            for (i--; i >= 0 && filename.charAt(i) != '\\' && filename.charAt(i) != '/'; i--) ;
            String s_dir = filename.substring(0, i);
            File dir = new File(s_dir);  // 输出的文件流
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream os = new FileOutputStream(filename);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            os.close();
            is.close();
        } catch (Throwable ex) {

        }
    }


    @SneakyThrows
    public void find(String name, int num, String v) {
        System.setProperty("webdriver.chrome.driver", "/Users/zhangzhiyong/program/chromedriver2/chromedriver");
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
                String src = idriver.findElement(By.xpath("//*[@id=\"mainImageWindow\"]/div[2]/div/div/div/img")).getAttribute("src");
                System.out.println(src);
                download(src, path + v + "/" + i + ".jpeg");
            } else {
                driver.findElement(By.xpath("//*[@id=\"navr\"]")).click();
                String src = driver.findElement(By.xpath("//*[@id=\"mainImageWindow\"]/div[2]/div/div/div/img")).getAttribute("src");
                System.out.println(src);
                download(src, path + v + "/" + i + ".jpeg");
            }

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        System.out.println("close");
        driver.close();
    }


    private WebElement getBingElement(WebDriver driver) {
        return imgList.stream().map(it -> {
            try {
                return driver.findElement(By.xpath(it));
            } catch (Throwable e) {
            }
            return null;
        }).filter(it -> null != it).findAny().get();
    }

}
