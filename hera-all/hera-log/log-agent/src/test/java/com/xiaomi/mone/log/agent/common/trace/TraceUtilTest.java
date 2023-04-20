package com.xiaomi.mone.log.agent.common.trace;

import com.xiaomi.hera.tspandata.TSpanData;
import org.junit.Assert;
import org.junit.Test;

public class TraceUtilTest {

    private static String spanStr = "";

    @Test
    public void toTSpanDataTest() {
        TSpanData tSpanData = TraceUtil.toTSpanData(spanStr);
        Assert.assertTrue(tSpanData != null && tSpanData.isSetKind() && tSpanData.isSetStatus()
                && tSpanData.isSetAttributes() && tSpanData.isSetEvents() && tSpanData.isSetLinks()
                && tSpanData.isSetParentSpanContext() && tSpanData.isSetExtra());
    }
}
