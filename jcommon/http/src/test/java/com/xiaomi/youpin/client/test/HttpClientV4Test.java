package com.xiaomi.youpin.client.test;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV4;
import com.xiaomi.data.push.client.Response;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/6/13
 */
public class HttpClientV4Test {


    @Test
    public void testHttps() {
        HttpClientV4 client = new HttpClientV4();
        Response res = client.get("https://www.oschina.net/", Maps.newHashMap(), 1000);
        System.out.println(res);
    }


    @Test
    public void testHttp() {
        HttpClientV4 client = new HttpClientV4();
        Response res = client.get("http://www.baidu.com/", Maps.newHashMap(), 1000);
        System.out.println(res);
    }
}
