package com.xiaomi.youpin.docean.plugin.test.lookup;

import com.xiaomi.youpin.docean.anno.Lookup;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.test.bo.Test;

/**
 * @author goodjava@qq.com
 * @date 2023/9/20 13:56
 */
@Service
public class LookupService {

    @Lookup
    public Test getTest() {
        return null;
    }


    public Test g() {
        return getTest();
    }


}
