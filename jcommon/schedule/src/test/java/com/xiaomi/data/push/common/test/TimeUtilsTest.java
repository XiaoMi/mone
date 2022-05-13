package com.xiaomi.data.push.common.test;

import com.xiaomi.data.push.common.TimeUtils;
import org.junit.Test;

public class TimeUtilsTest {


    @Test
    public void testMoreThanOneHour() {
        System.out.println(TimeUtils.moreThanOneHour(1576208017000L));
    }
}
