package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.common.UdsException;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2022/11/24 08:56
 */
public class ExceptionTest {


    @Test
    public void testException() {
        UdsException ue = new UdsException("error");
        System.out.println(ue instanceof RuntimeException);
    }
}
