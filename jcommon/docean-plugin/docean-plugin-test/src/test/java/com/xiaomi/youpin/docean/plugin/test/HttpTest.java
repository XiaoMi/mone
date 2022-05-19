package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.plugin.http.Http;
import com.xiaomi.youpin.docean.plugin.http.Response;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/6/28
 */
public class HttpTest {


    @Test
    public void testGet() {
        Http http = new Http();
        Response res = http.get("http://www.baidu.com", Maps.newHashMap(), 1000);
        System.out.println(res);
    }
}
