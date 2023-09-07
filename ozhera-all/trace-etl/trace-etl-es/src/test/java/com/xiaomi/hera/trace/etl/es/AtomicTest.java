package com.xiaomi.hera.trace.etl.es;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author goodjava@qq.com
 * @date 2023/9/7 14:05
 */
public class AtomicTest {


    @Test
    public void testGetAndUpdate() {
        AtomicInteger ai = new AtomicInteger(1);
        Assert.assertEquals(1,ai.getAndUpdate(v->12));
    }

}
