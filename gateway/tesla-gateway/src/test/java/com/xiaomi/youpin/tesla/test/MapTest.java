/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.tesla.test;

import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 4/5/21
 */
public class MapTest {
    
    
    @Test
    public void testRemove() {
        ConcurrentHashMap<String,String> m = new ConcurrentHashMap<>();
        m.remove(null);
    }


    @Test
    public void testCompute() {
        ConcurrentHashMap<String, String> m = new ConcurrentHashMap<>();
//        m.put("abc", "456");
        String r = m.compute("abc", (k, v) -> {
            if (null == v) {
                return "123";
            }
            return v;
        });
        System.out.println(r);

        System.out.println(m);
    }
}
