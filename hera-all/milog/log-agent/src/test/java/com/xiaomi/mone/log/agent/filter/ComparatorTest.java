package com.xiaomi.mone.log.agent.filter;

import com.xiaomi.mone.log.agent.input.AppLogInput;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/3/29 15:11
 */
public class ComparatorTest {

    @Test
    public void test1() {
        AppLogInput newInput = new AppLogInput();
        newInput.setLogPattern("/fsdfdsf/sfsdfs");
        newInput.setLogSplitExpress("/fsfds/sdfsdf");
        AppLogInput oldInput = new AppLogInput();
        oldInput.setLogSplitExpress("/fsfds/sdfsdf");
        Assert.assertEquals(false, newInput.equals(oldInput));
    }

}
