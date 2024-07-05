package com.xiaomi.youpin.docean.test;

import com.xiaomi.youpin.docean.common.MutableObject;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2024/7/5 08:18
 */
public class MapTest {

    @Test
    public void testMap() {
        MutableObject mo = new MutableObject();
        ConcurrentHashMap<String,MutableObject> map = new ConcurrentHashMap<>();
        mo.setObj("123");
        map.put("aa",mo);
        MutableObject e = map.compute("a", (k, v) -> {
            if (null != v) {
                v.setObj("234");
            }
            return v;
        });
        System.out.println(e);
    }
}
