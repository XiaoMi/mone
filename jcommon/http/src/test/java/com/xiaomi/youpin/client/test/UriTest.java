package com.xiaomi.youpin.client.test;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author goodjava@qq.com
 * @date 2020/6/13
 */
public class UriTest {

    @Test
    public void testPort() throws URISyntaxException {
        URI uri = new URI("https://www.baidu.com");
        System.out.println(uri.getPort());

    }
}
