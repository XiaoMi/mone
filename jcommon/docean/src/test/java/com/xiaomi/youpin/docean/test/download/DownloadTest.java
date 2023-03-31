package com.xiaomi.youpin.docean.test.download;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2023/3/27 09:58
 */
public class DownloadTest {

    @Test
    public void testName() {
        String name = "../../..\\c";
        System.out.println(name.contains(".."));
        System.out.println(name.contains("/"));
        System.out.println(name.contains("\\"));
        Assert.assertTrue(name.contains(".."));
    }
}
