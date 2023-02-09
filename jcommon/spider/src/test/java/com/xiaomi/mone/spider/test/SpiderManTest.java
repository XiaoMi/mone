package com.xiaomi.mone.spider.test;

import com.beust.jcommander.internal.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.spider.SpiderMan;
import com.xiaomi.mone.spider.GeneralSpiderMan;
import com.xiaomi.mone.spider.WeiBoSpiderMan;
import com.xiaomi.mone.spider.ZhiHuSpiderMan;
import com.xiaomi.mone.spider.util.UrlData;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class SpiderManTest {

    final Base64.Decoder decoder = Base64.getDecoder();

    @Test
    public void testWeiBo() throws IOException {
        SpiderMan spiderMan = new WeiBoSpiderMan(true);
        System.in.read();
        spiderMan.closeDriver();
    }

    @Test
    public void testStock() throws IOException {
        GeneralSpiderMan m = new GeneralSpiderMan(false, Lists.newArrayList("stock"));
        System.in.read();
        m.closeDriver();
    }

    @Test
    public void testOschina() throws IOException {
        GeneralSpiderMan m = new GeneralSpiderMan(false, Lists.newArrayList("oschina"));
        System.in.read();
        m.closeDriver();
    }

    @Test
    public void testCnbeta() throws IOException {
        GeneralSpiderMan m = new GeneralSpiderMan(false, Lists.newArrayList("cnbeta"), false);
        System.in.read();
        m.closeDriver();
    }

    @Test
    public void testZhiHu() throws InterruptedException {
        SpiderMan spiderMan = new ZhiHuSpiderMan();

        List<UrlData> rt = spiderMan.getList("");
        if (Objects.nonNull(rt)) {
            rt.forEach(entity -> {
                System.out.println("topic:" + entity.getContent() + "  link:" + entity.getUrl());
            });
        }
    }

    @Test
    public void testBase64Decoder() {

        String origin = "{\"attached_info_bytes\":\"CjgIABADGgg3Mjk1MjU2NSCJlK+MBjCFATiCD0AAcgk0OTgwMjA0MDB4AKoBCWJpbGxib2FyZNIBAA==\"}";
        Map<String, String> map = new Gson().fromJson(origin, new TypeToken<HashMap<String, String>>() {
        }.getType());

        String baseUrl = map.get("attached_info_bytes");

        String url = new String(decoder.decode(baseUrl.getBytes()));

        char[] baseArr = url.toCharArray();

        char[] urlArrReverse = new char[9];
        char[] urlArr = new char[9];
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
        System.out.println(new String(urlArr));
    }
}
