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

package com.youpin.xiaomi.tesla.test;

import com.xiaomi.youpin.infra.rpc.Result;
import org.junit.Test;

/**
 * @Author goodjava@qq.com
 * @Date 2021/9/2 14:35
 */
public class ResultTest {


    @Test
    public void testResult() throws ClassNotFoundException {
        Result<String> r = Result.success("abc");

        System.out.println(r.getClass().getName());


        Class clazz = Class.forName("com.xiaomi.youpin.infra.rpc.Result");


        if (r.getClass().getName().equals("com.xiaomi.youpin.infra.rpc.Result")) {
            Object rr = clazz.cast(r);
            System.out.println(rr);
        }
    }

}
