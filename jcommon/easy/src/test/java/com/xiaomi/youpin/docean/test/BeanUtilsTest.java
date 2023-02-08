package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.bo.A;
import com.xiaomi.youpin.docean.bo.Z;
import com.xiaomi.youpin.docean.common.BeanUtils;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2022/11/27 10:32
 */
public class BeanUtilsTest {



    @Test
    public void testCop() {
        Z z = new Z(222);
        A a = new A();
        a.setId(123);
        BeanUtils.copy(z,a);
        System.out.println(z);
    }
}
