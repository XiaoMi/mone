package com.xiaomi.mone.spider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.spider.util.UrlData;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ZhiHuSpiderMan implements SpiderMan {

    private static String zhihuLogin = "https://www.zhihu.com/signin?next=%2Fhot";
    private static String zhihuHot = "https://www.zhihu.com/hot";

    private static String zhihuHotUnLogin = "https://www.zhihu.com/billboard";

    private static String ZhihuQuestionAddrPre = "https://www.zhihu.com/question/";

    private static boolean userRemoteDriver = true;


    final Base64.Decoder decoder = Base64.getDecoder();

    private static final int RETRY_TIME = 3;
    private static final String cookie = "_zap=6db7a3b7-04fb-46cd-9721-897cb4749701; _xsrf=41ef7b0e-26c4-491c-b7ac-5d6c05b3c0ae; d_c0=AGDfqgvh4RKPTpK6XnlAt29TQP9pl21iMIo=|1617176134; __snaker__id=YOp3cEJCmaTtEpbK; _9755xjdesxxd_=32; YD00517437729195%3AWM_TID=zUWV5%2FxvSVpBBRBQEVIqtB901WZt5MiX; YD00517437729195%3AWM_NI=8byqr0MK1JD1qEdj2eWUxtKhjHxw3Z9LraXB1b23Q7HD6gfx4cdOFwNTQtnHUUE6BoDXKRFnmp45SAY8YSK3sBkcas6X6N09KeabuAqxaLlQOLNe9oebZuze5zHx901FZTQ%3D; YD00517437729195%3AWM_NIKE=9ca17ae2e6ffcda170e2e6eeb4ae4f91bda58fd152f2868ba6c54f879f9abbaa79b08d9f86f67098bda5d6c12af0fea7c3b92afcbeb792f13c8f95ffabc73bb39bac8bb16f958bb895ca64aa98b898b252fb94be82db4af3befed4d43d8c9cfa88e574f8bdacb0c57d8e8c9f95ed44978b8bd7c165888885b3c566afb0fcd6d24b9be897b4ca39b19ea584e479f688faafb33389bbaf83f84bb3a8afadef5b8d948286bc65a7be9696f96ff7a68899bc3cf39a96b5d437e2a3; Hm_lvt_98beee57fd2ef70ccdd5ca52b9740c49=1636524177,1636524202,1636524231,1636524298; capsion_ticket=2|1:0|10:1636524529|14:capsion_ticket|44:ODhlMTY2ZmUzZDYwNDllY2E4MDk0YjIxZGFjYTE1OTQ=|c24c9490114639d4aeedb471df90b158fb482d43087c8465e057c3116e59339d; SESSIONID=CThz7PfKlsWXaTnAqWsR7fxwwnX1bMj8Gi7yrIPEVx8; JOID=Vl4WAksKDkHKhelSEg542XGJDbUHS18rsba7MVtifxOQ5bk-Vvxoya6D7lURc6y7jRTdmc9WKGU_oBttvRCiUsE=; osd=V1wSAEgLDEXIhuhQFgx72HOND7YGSVspsre5NVlhfhGU57o_VPhqyq-B6lcScq6_jxfcm8tUK2Q9pBluvBKmUMI=; l_n_c=1; o_act=login; ref_source=other_https://www.zhihu.com/signin?next=/; r_cap_id=MzRkYWEwNDcxN2Q3NDVmZmE4N2YzODAxM2JmMmY1OGY=|1636524796|1bb0d98195e1f84194736d2621bde0956b710841; cap_id=NzhmNzRhYmFkMTVjNGU1OWFkNmEwMGYzNGRhZmY0ZTA=|1636524796|d4d3e1e32ee06b0a9da755c10154aa63dfe39a6b; l_cap_id=NjExMGZhODk0MDIxNGU5YWIxZmIyYzVjOWM3MjAwMWI=|1636524796|a61f3951f776defdb96dda42d6113aac5ffd6dbb; n_c=1; captcha_session_v2=2|1:0|10:1636524809|18:captcha_session_v2|88:c2ZMTm5SYzRnZnlnanY3c0VzV2JnakZtU1NOYkc2cjVqUk1TR3l5Wi9GeUl2ZEI5V012d1JYckg4SXlvVkN2Kw==|a2d7c10411d78fc1d9e6c96318f2655402d630ec8d9235585294df2f3a60b643; captcha_ticket_v2=2|1:0|10:1636524813|17:captcha_ticket_v2|704:eyJ2YWxpZGF0ZSI6IkNOMzFfS1FCMnBaejZURlpKTFNUQXRtd3I0dnlsVzJRRlM5aC1fOGRnUlV3LVNrd2MydWpZTmg4VWMwQ2piOWlRWW5hTnVjSVMudjlUcUZhRlFkaUd0QlUwRExhYWlZY0VodEtmNS0yMHhMQlpMQ0NvaFh1S2xZenhWMnVjajFFRS1meGFOOF9xT1Mwc29ZajI0ZTRIN0l3S0Eyc0YudDVlbGgud1V6TmxWY2E5cWt6U3ZlekZHMjBINEdhVklIMnl2S1ROYkFsSGtJdjBBODI3cklKT2hDZ3p6TFB3VlA0NGNxMVI2eGJ6SFM1R3NwVS1BSmRWT1F1TUlvOVV3akJEZV9oWTFxVVFhUjJ4NnhWMVFXTG9sSFc0T2FkQlc0RElvck1yYkFJVS4xRkRhTFIxZ1hxVmJMT3hkTi5jN0EySXZhbFFZSWdVUVNFYjVIOVp0UnJrVDFHUS52YVN3UWNXMVlFcHNWY0NYNHMySndmWHhTTlRkai1uLnJ2TVhfT1hUX1k2eWtETlBDZW5JUmhWV3NoWWdWeENSLkhWNU90RTFvb3FlRXpfTXl4SzlrUmE3SGtOQzRmVUlicV9OaUEwZG5SNkU1aUNzRzZRRV9tNjl0c1daZ1dnR1BLMGRra3QuUXFVSkZ5TUlKMWhaTFNFUzI5VVRRZzB6RE1IQVlpMyJ9|351e190f0276be2f9995d71d6809d6f4f811aa4625a6b1a462ab3e10ce22b47f; z_c0=2|1:0|10:1636525142|4:z_c0|92:Mi4xSklBUENnQUFBQUFBWU4tcUMtSGhFaVlBQUFCZ0FsVk5Wcko0WWdDQkxLWVFfTktyX0I0bjZacF9PN3JpMk52ekxn|92913de84788b34d08fba56ff4018e0a422cb8790ac436082641c969fd50d7c4; tst=h; Hm_lpvt_98beee57fd2ef70ccdd5ca52b9740c49=1636525154; gdxidpyhxdE=UelYPRe%2BIKG%5CacV6gAUoheT0b5GzwrBGx7%5CawlsdRS7M9VOkbJAVGBPHV6O6rTrux5%2BC0tOVRr7UCPL4j6M12jAd8dW6z%2FTiYfRyxoVd9%2FbROepE0KVSZVeiREj6YV71pR7ojE3BxzvzKvNCCu4V0fNjgKP5osVTMBC6y6iRWyHWBWZd%3A1636528448427; KLBRSID=2177cbf908056c6654e972f5ddc96dc2|1636528354|1636523262";
    private CopyOnWriteArrayList<UrlData> list = new CopyOnWriteArrayList<>();
    private CopyOnWriteArrayList<UrlData> viewList = new CopyOnWriteArrayList<>();

    private WebDriver driver;

    private ScheduledExecutorService timer = Executors.newSingleThreadScheduledExecutor();

    public ZhiHuSpiderMan() throws InterruptedException {
        refreshZhiHuHot();
        scheduleRefreshZhiHuHot();
    }

    public void scheduleRefreshZhiHuHot() {
        System.setProperty("webdriver.chrome.driver", "/tmp/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.setHeadless(true);

        if (userRemoteDriver) {
            ChromeOptions cap = new ChromeOptions();
            cap.addArguments("--headless");
            cap.addArguments("--disable-gpu","--disable-dev-shm-usage");
            try {
                driver = new RemoteWebDriver(new URL("http://127.0.0.1/"), cap);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            driver = new ChromeDriver(options);
        }
        ZhiHuTask task = new ZhiHuTask();
        timer.scheduleAtFixedRate(task, 1, 10, TimeUnit.SECONDS);
    }


    public void refreshZhiHuHot() throws InterruptedException {
        List<WebElement> hotList;
        for (int i = 0; i < RETRY_TIME; i++) {
            try {
                driver.get(zhihuHotUnLogin);

                //热搜内容都在card中
                WebElement card = driver.findElement(By.className("Card"));

                hotList = card.findElements(By.className("HotList-item"));
            } catch (Exception e) {
                Thread.sleep(TimeUnit.SECONDS.toMillis(3));
                continue;
            }
            if (Objects.isNull(hotList) || hotList.isEmpty()) {
                //说明没到目标页,再重试
                continue;
            }
            //有数据才清空
            list.clear();
            hotList.forEach(hotItem -> {
                String title = hotItem.findElement(By.className("HotList-itemBody")).findElement(By.className("HotList-itemTitle")).getText();
                String base64Url = hotItem.getAttribute("data-za-extra-module");
                String url = parseUrl(base64Url);
                log.info("zhihu get {} {}", title, url);
                list.add(new UrlData(title, url));
            });

            if (hotList.size() > 0) {
                synchronized (this.viewList) {
                    this.viewList.clear();
                    this.viewList.addAll(list);
                }
                break;
            }
        }
    }

    private class ZhiHuTask implements Runnable {

        private final SimpleDateFormat dateFormat;

        private ZhiHuTask() {
            this.dateFormat = new SimpleDateFormat("HH:mm:ss");
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("zhihu spider begin：" + dateFormat.format(new Date()));
            try {
                refreshZhiHuHot();
            } catch (Throwable e) {
                log.warn("refresh zhihu hot list err:", e);
            }
            log.info("zhihu spider end：" + dateFormat.format(new Date()));
        }
    }

    private List<Cookie> parseCookies(String cookieStr) {
        String[] cookiePairs = cookieStr.split(";");
        List<Cookie> cookies = new ArrayList<>(cookiePairs.length);

        for (String cookiePair : cookiePairs
        ) {
            String[] kv = cookiePair.split("=", 2);
            Cookie cookie = new Cookie(kv[0], kv[1]);
            cookies.add(cookie);
        }
        return cookies;
    }

    private String parseUrl(String origin) {

        Map<String, String> map = new Gson().fromJson(origin, new TypeToken<HashMap<String, String>>() {
        }.getType());

        String baseUrl = map.get("attached_info_bytes");

        char[] urlArr;
        try {
            String url = new String(decoder.decode(baseUrl.getBytes()));

            char[] baseArr = url.toCharArray();

            char[] urlArrReverse = new char[9];
            urlArr = new char[9];
            for (int index = baseArr.length - 1; index > 0; index--) {
                if (baseArr[index] == 'x') {
                    int flag = index - 1;
                    int start = 0;
                    while (baseArr[flag] != '\t') {
                        urlArrReverse[start] = baseArr[flag];
                        start++;
                        flag--;
                    }
                    break;
                }
            }
            int start = 0;
            for (int i = urlArrReverse.length - 1; i >= 0; i--) {
                urlArr[start] = urlArrReverse[i];
                start++;
            }
        } catch (Exception e) {
            return "";
        }

        //例:https://www.zhihu.com/question/498020400?utm_division=hot_list_page
        String questionNo = new String(urlArr);
        log.info("questionNo:%s" + questionNo);
        return ZhihuQuestionAddrPre + questionNo + "?utm_division=hot_list_page";
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
