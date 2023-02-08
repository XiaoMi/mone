package com.xiaomi.mone.umami;

import com.google.gson.Gson;
import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.mone.umami.bo.Event;
import com.xiaomi.mone.umami.bo.View;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tsingfu
 */
@Data
public class Umami {

    private static String url = "";
    private static String origin = "";
    private static String referer = "";

    private static String website = "";

    public static String sendEvent(String eventType, String viewName) {
        Gson gson = new Gson();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", " */*");
        header.put("Accept-Language", " zh-CN,zh;q=0.9,en;q=0.8");
        header.put("Cache-Control", "no-cache");
        header.put("Content-Type", "application/json");
        header.put("Origin", origin);
        header.put("Pragma", "no-cache");
        header.put("Referer", referer);
        header.put("Sec-Fetch-Dest", "empty");
        header.put("Sec-Fetch-Mode", "cors");
        header.put("Sec-Fetch-Site", "cross-site");
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
        header.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "JavaSdk");
        return HttpClientV2.post(url, gson.toJson(new Event(website, eventType, viewName)), header, 1000);
    }

    public static String sendView (String viewName) {
        Gson gson = new Gson();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", " */*");
        header.put("Accept-Language", " zh-CN,zh;q=0.9,en;q=0.8");
        header.put("Cache-Control", "no-cache");
        header.put("Content-Type", "application/json");
        header.put("Origin", origin);
        header.put("Pragma", "no-cache");
        header.put("Referer", referer);
        header.put("Sec-Fetch-Dest", "empty");
        header.put("Sec-Fetch-Mode", "cors");
        header.put("Sec-Fetch-Site", "cross-site");
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
        header.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "JavaSdk");
        return HttpClientV2.post(url, gson.toJson(new View(website, viewName)), header, 1000);
    }

    public static String getUrl() {
        return url;
    }

    public static void setUrl(String url) {
        Umami.url = url;
    }

    public static String getOrigin() {
        return origin;
    }

    public static void setOrigin(String origin) {
        Umami.origin = origin;
    }

    public static String getReferer() {
        return referer;
    }

    public static void setReferer(String referer) {
        Umami.referer = referer;
    }

    public static String getWebsite() {
        return website;
    }

    public static void setWebsite(String website) {
        Umami.website = website;
    }
}
