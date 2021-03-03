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

package com.xiaomi.youpin.docean.plugin.test;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.plugin.http.Http;
import com.xiaomi.youpin.docean.plugin.http.Response;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/6/28
 */
public class HttpTest {


    @Test
    public void testGet() {
        Http http = new Http();
        Response res = http.get("http://www.baidu.com", Maps.newHashMap(), 1000);
        System.out.println(res);
    }
}
