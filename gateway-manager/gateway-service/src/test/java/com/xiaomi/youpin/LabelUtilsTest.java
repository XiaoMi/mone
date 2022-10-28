package com.xiaomi.youpin;

import com.xiaomi.youpin.gwdash.common.LabelUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class LabelUtilsTest {


    @Test
    public void testLabelUtils() {
        Map<String, String> m = LabelUtils.convert("a=1,b=2,c=3");
        System.out.println(m);
        Assert.assertEquals(3, m.size());
    }
}
