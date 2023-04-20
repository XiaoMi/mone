package com.xiaomi.mone.log.manager.filter.cons;

import com.xiaomi.mone.log.api.enums.RateLimitEnum;
import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.HashMap;

public class RateLimitConsTest extends TestCase {

    public void testConsRateimitFilterDefine() {
        FilterDefine filterDefine = FilterDefine.consRateLimitFilterDefine("FAST");
        System.out.println(filterDefine);
    }

    public void testConsTailRate() {
        FilterDefine d1 = new FilterDefine();
        FilterDefine d2 = new FilterDefine();
        d1.setCode(Common.RATE_LIMIT_CODE + 3);
        d1.setArgs(new HashMap<String, String>() {{
            put(Common.PERMITS_PER_SECOND, "100");
        }});
        String s = RateLimitEnum.consTailRate(Arrays.asList(d2, d1));
        System.out.println(s);
    }
}