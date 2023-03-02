package com.xiaomi.mone.log.agent.filter;

import com.xiaomi.mone.log.api.filter.Common;
import com.xiaomi.mone.log.api.model.meta.FilterConf;
import com.xiaomi.mone.log.api.model.meta.FilterDefine;
import junit.framework.TestCase;

import java.util.HashMap;

public class FilterTransTest extends TestCase {

    public void testFilterConfTrans() {
        FilterDefine filterDefine = new FilterDefine();
        filterDefine.setCode(Common.RATE_LIMIT_CODE + 0);
        filterDefine.setArgs(new HashMap<String, String>() {{
            put(Common.PERMITS_PER_SECOND, "100");
        }});
        FilterConf filterConf = FilterTrans.filterConfTrans(filterDefine);
        System.out.println(filterConf);
    }
}