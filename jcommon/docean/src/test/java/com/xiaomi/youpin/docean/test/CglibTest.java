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

package com.xiaomi.youpin.docean.test;

import com.google.common.collect.ImmutableMap;
import com.xiaomi.youpin.docean.test.bo.M;
import com.xiaomi.youpin.docean.test.bo.TResult;
import net.sf.cglib.beans.BeanMap;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/7/5
 */
public class CglibTest {


    class CT {
        public String ct() {
            return "ct";
        }
    }

    @Test
    public void testCglib() {

    }

    @Test
    public void testMapBean() {
        M m  = new M();
        m.setId(123);
        TResult r = new TResult();
        r.setData(m);
        BeanMap bm = BeanMap.create(r);
        bm.put("code",999);
        bm.put("data", ImmutableMap.of("id",678));
        System.out.println(r);
    }
}
