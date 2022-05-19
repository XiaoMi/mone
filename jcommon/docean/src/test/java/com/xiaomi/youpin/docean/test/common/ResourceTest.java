package com.xiaomi.youpin.docean.test.common;

import com.xiaomi.youpin.docean.common.ClassPathResource;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public class ResourceTest {


    @Test
    public void testResource() throws IOException {
        ClassPathResource r = new ClassPathResource("xml/b.xml");
        System.out.println(r.getInputStream());
    }
}
