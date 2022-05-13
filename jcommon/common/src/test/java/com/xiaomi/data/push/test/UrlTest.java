package com.xiaomi.data.push.test;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author goodjava@qq.com
 * @date 2022/4/6 17:25
 */
public class UrlTest {

    @Test
    public void testUrl() throws MalformedURLException {
        String match = ".baidu.com";
        String str = "www.xiaomiyoupin.abc123.com?id=123";
        if (!str.startsWith("http")) {
            str = "http://" + str;
        }
        URL url = new URL(str);
        System.out.println(url.getHost());
        System.out.println(url.getHost().endsWith(match));
    }
}
