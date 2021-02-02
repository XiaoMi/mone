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

package com.xiaomi.youpin.client.test;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.HttpClientV4;
import com.xiaomi.data.push.client.Response;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2020/6/13
 */
public class HttpClientV4Test {


    @Test
    public void testHttps() {
        HttpClientV4 client = new HttpClientV4();
        Response res = client.get("https://www.oschina.net/", Maps.newHashMap(), 1000);
        System.out.println(res);
    }


    @Test
    public void testHttp() {
        HttpClientV4 client = new HttpClientV4();
        Response res = client.get("http://www.baidu.com/", Maps.newHashMap(), 1000);
        System.out.println(res);
    }
}
