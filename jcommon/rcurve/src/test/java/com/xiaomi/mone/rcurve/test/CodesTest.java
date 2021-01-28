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

package com.xiaomi.mone.rcurve.test;

import com.xiaomi.data.push.uds.codes.GsonCodes;
import com.xiaomi.data.push.uds.codes.HessianCodes;
import org.junit.Test;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 1/22/21
 */
public class CodesTest {

    @Test
    public void testGsonCodes() {
        GsonCodes codes = new GsonCodes();
        Obj obj = new Obj();
        obj.setId(1);
        obj.setName("zzy");
        byte[] data = codes.encode(obj);
        Obj obj2 = codes.decode(data, obj.getClass());
        System.out.println(obj2);
        Assert.notNull(obj2, "null");
    }

    public static <T> Map<String, Object> beanToMap(T bean) {
        return BeanMap.create(bean);
    }


    @Test
    public void testHessianCodes() {
        HessianCodes codes = new HessianCodes();
        Obj obj = new Obj();
        obj.setId(1);
        obj.setName("zzy");
        byte[] data = codes.encode(obj);
        Obj obj2 = codes.decode(data, obj.getClass());
        System.out.println(obj2);
        Assert.notNull(obj2, "null");


        Map<String, Object> m = beanToMap(obj);
        data = codes.encode(m);
        Object m2 = codes.decode(data, Map.class);
        System.out.println(m2);


    }
}
