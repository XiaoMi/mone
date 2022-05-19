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

    private static String org = "";
    private static String referer = "";

    public static String sendEvent(Event event) {
        Gson gson = new Gson();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", " */*");
        header.put("Accept-Language", " zh-CN,zh;q=0.9,en;q=0.8");
        header.put("Cache-Control", "no-cache");
        header.put("Content-Type", "application/json");
        header.put("Origin", org);
        header.put("Pragma", "no-cache");
        header.put("Referer", referer);
        header.put("Sec-Fetch-Dest", "empty");
        header.put("Sec-Fetch-Mode", "cors");
        header.put("Sec-Fetch-Site", "cross-site");
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
        header.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "JavaSdk");
        return HttpClientV2.post(url, gson.toJson(event), header, 1000);
    }

    public static String sendView (View view) {
        Gson gson = new Gson();
        Map<String, String> header = new HashMap<>();
        header.put("Accept", " */*");
        header.put("Accept-Language", " zh-CN,zh;q=0.9,en;q=0.8");
        header.put("Cache-Control", "no-cache");
        header.put("Content-Type", "application/json");
        header.put("Origin", org);
        header.put("Pragma", "no-cache");
        header.put("Referer", referer);
        header.put("Sec-Fetch-Dest", "empty");
        header.put("Sec-Fetch-Mode", "cors");
        header.put("Sec-Fetch-Site", "cross-site");
        header.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.127 Safari/537.36");
        header.put("sec-ch-ua", "\" Not A;Brand\";v=\"99\", \"Chromium\";v=\"100\", \"Google Chrome\";v=\"100\"");
        header.put("sec-ch-ua-mobile", "?0");
        header.put("sec-ch-ua-platform", "JavaSdk");
        return HttpClientV2.post(url, gson.toJson(view), header, 1000);
    }
}
