package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.common.FlagCal;
import com.xiaomi.data.push.uds.po.Permission;
import org.junit.Test;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/23 15:15
 */
public class FlagCalTest {


    @Test
    public void testCal() {
        FlagCal cal = new FlagCal(0);
        cal.enable(Permission.IS_REQUEST);
        System.out.println(cal.isTrue(Permission.IS_REQUEST));
        System.out.println(cal.getFlag());
    }

}
