package run.mone.local.docean.service.tool;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v122.network.Network;
import org.openqa.selenium.devtools.v122.network.model.Request;
import org.openqa.selenium.remote.Response;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author liuchuankang
 * @date 2024/02/27
 */
@Slf4j
@Service
public class ChromeService implements ToolService {

    private AtomicBoolean isInit = new AtomicBoolean();

    private WebDriver driver;

    @Value("${chrome.driver.path}")
    private String chromeDriverPath;

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public void newInit() {
        if (isInit.compareAndSet(false, true)) {
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            // 创建 ChromeOptions 实例并设置默认 URL
            ChromeOptions options = new ChromeOptions();
            options.setPageLoadStrategy(PageLoadStrategy.EAGER); // 设置页面加载策略
            options.addArguments("--start-maximized"); // 最大化浏览器窗口
            options.addArguments("homepage=https://www.example.com");
//            options.addArguments("headless");
//            options.addArguments("disable-gpu");
//            options.addArguments("disable-dev-shm-usage");
//            options.addArguments("disable-plugins");
//            // 禁用java
//            options.addArguments("disable-java");
//            // 以最高权限运行
//            options.addArguments("no-sandbox");

            // 创建 Chrome 浏览器实例
            driver = new ChromeDriver(options);
//            log.info("新打开chrome浏览器，chromeDriver={}",new Gson().toJson(driver));
        }
    }


    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public boolean open(String url) {
        newInit();
//        driver.get(url);
        Set<String> before = driver.getWindowHandles();
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        Response response = (Response) jsExecutor.executeScript("window.open('" + url + "')");
        Set<String> after = driver.getWindowHandles();
        after.removeAll(before);
        driver.switchTo().window((String) after.toArray()[0]);
        return true;
    }

    public String openWithCookie(String url,String cookieStr) throws IOException, AWTException {
        newInit();
        LocalDate localDate = LocalDate.now().plusDays(2);
        System.out.println("local:"+localDate.getYear());
        ZoneId zoneId = ZoneId.systemDefault();
        Date date = Date.from(localDate.atStartOfDay().atZone(zoneId).toInstant());
        URL url1 = new URL(url);
        driver.get(url);
        WebDriver.Options manage = driver.manage();
        if(StringUtils.isNotBlank(cookieStr)){
            String[] split = cookieStr.split(";");
            if(split.length>1){
                for (String cookieKV : split) {
                    String[] split1 = cookieKV.split("=");
                    if(split1.length == 2){
                        // 创建 Cookie 对象
                        Cookie cookie = new Cookie(split1[0], split1[1],url1.getHost(),"/",date);
                        // 添加 Cookie 到浏览器
                        manage.addCookie(cookie);
                    }
                }
            }
        }
        driver.get(url);
        WebElement webElement = driver.findElement(By.xpath("/html"));
        return webElement.getText();

    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public String getTitle() {
        if (isInit.get()) {
            String title = driver.getTitle();
            log.info("chrome 获取title={}", title);
            return title;
        }
        return null;
    }
    public List<String> getImageList() {
        List<WebElement> images = driver.findElements(By.tagName("img"));
        return images.stream().map(image->image.getAttribute("src")).collect(Collectors.toList());
    }
    public String getAllText() {
        return  driver.findElement(By.tagName("body")).getText();
    }
    public String clickText(String text) {
        // 查找页面上的文本元素
        WebElement textElement=driver.findElement(By.xpath("//span[contains(text(),'"+text+"')]"));
        // 点击文本元素
        textElement.click();
        WebElement webElement = driver.findElement(By.xpath("/html"));
        return webElement.getText();
    }
    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public String getContent(String title, String url) {
        log.info("chrome 获取页签内容title={},url={}", title, url);

        if (isInit.get()) {
            String currentWindowHandle = driver.getWindowHandle();
            Set<String> windowHandles = driver.getWindowHandles();
            for (String windowHandle : windowHandles) {
                driver.switchTo().window(windowHandle);
                if ((StringUtils.isNotBlank(title) && driver.getTitle().contains(title))
                        || StringUtils.isNotBlank(url) && driver.getCurrentUrl().contains(url)) {
                    String pageSource = driver.getPageSource();
                    driver.switchTo().window(currentWindowHandle);
                    log.info("chrome 获取页签结果content={}", pageSource);
                    return pageSource;
                }
            }
            driver.switchTo().window(currentWindowHandle);
            String pageSource = driver.getPageSource();
            log.info("chrome 获取页签结果content={}", pageSource);
            return pageSource;
        }
        return null;
    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public boolean close() {
        if (!isInit.get()) {
            return true;
        }
        if (driver.getWindowHandles().size() == 1 && isInit.compareAndSet(true, false)) {
            log.info("chrome quit浏览器");
            driver.quit();
        } else {
            Set<String> windowHandles = driver.getWindowHandles();
            String windowHandle = driver.getWindowHandle();
            windowHandles.remove(windowHandle);
            log.info("chrome close页签");
            driver.close();
            driver.switchTo().window(windowHandles.stream().findFirst().get());
        }
        return true;
    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public boolean quit() {
        if (isInit.compareAndSet(true, false)) {
            log.info("chrome quit浏览器");
            driver.quit();
        }
        return true;
    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public String getCurrentUrl() {
        newInit();
        String currentUrl = driver.getCurrentUrl();
        log.info("chrome currentUrl={}", currentUrl);
        return currentUrl;
    }

    /**
     * @author liuchuankang
     * @date 2024/02/27
     */
    public List<String> list() {
        newInit();
        String currentWindowHandle = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        List<String> list = new ArrayList<>();
        // 遍历所有窗口句柄
        for (String windowHandle : windowHandles) {
            driver.switchTo().window(windowHandle);
            list.add(driver.getTitle());
        }
        driver.switchTo().window(currentWindowHandle);
        log.info("chrome list={}", new Gson().toJson(list));
        return list;
    }


    public void takeFullScreenShot(String filename) throws IOException, AWTException {
        // 获取浏览器窗口的尺寸
        org.openqa.selenium.Dimension size = driver.manage().window().getSize();

        // 创建一个Robot对象来截取屏幕
        Robot robot = new Robot();

        // 获取屏幕的分辨率
        Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

        // 计算需要截取的屏幕区域
        int width = size.getWidth();
        int height = size.getHeight();

        // 创建一个BufferedImage来保存截图
        BufferedImage fullScreenshot = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = fullScreenshot.getGraphics();

        // 截取屏幕并绘制到BufferedImage上
        g.drawImage(robot.createScreenCapture(screenSize), 0, 0, null);

        // 释放Graphics对象
        g.dispose();
        // 保存截图到文件
        ImageIO.write(fullScreenshot, "png", new File(filename));
    }


    public void networkMonitor(String url) {
        // 获取 DevTools 并创建一个会话
        DevTools devTools = ((ChromeDriver) driver).getDevTools();
        devTools.createSession();

        // 启用网络监控
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));

        // 添加网络请求监听器
        devTools.addListener(Network.requestWillBeSent(), request -> {
            Request req = request.getRequest();
//            System.out.println("Request URL: " + req.getUrl());
//            System.out.println("Request Method: " + req.getMethod());
        });

        // 添加网络响应监听器
        devTools.addListener(Network.responseReceived(), response -> {
            org.openqa.selenium.devtools.v122.network.model.Response res = response.getResponse();
            System.out.println("Response URL: " + res.getUrl());

            if(true){
             return;
            }

//            System.out.println("Response Status: " + res.getStatus());

            Command<Network.GetResponseBodyResponse> getResponseBodyCommand = Network.getResponseBody(response.getRequestId());

            Network.GetResponseBodyResponse responseBody = devTools.send(getResponseBodyCommand);
            // 检查响应体是否存在
            if (responseBody.getBase64Encoded()) {
                // 如果响应体是base64编码的，需要解码
                byte[] decodedBody = Base64.getDecoder().decode(responseBody.getBody());
                // 处理解码后的响应体
                System.out.println("Decoded response body: " + new String(decodedBody, StandardCharsets.UTF_8));
            } else {
                // 如果响应体不是base64编码的，直接输出
                System.out.println("--->");
                System.out.println("Response body: " + responseBody.getBody());
            }


        });
        // 打开一个网页
        driver.get(url);
    }



}
