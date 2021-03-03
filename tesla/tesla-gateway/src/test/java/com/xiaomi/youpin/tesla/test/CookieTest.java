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

import com.xiaomi.youpin.gateway.common.Utils;
import com.xiaomi.youpin.gateway.service.UserService;
import org.apache.thrift.TException;
import org.junit.Test;

/**
 * @author 丁春秋
 */
public class CookieTest {
    @Test
    public void test() throws TException {
        String cookie = "";
        UserService userService = new UserService();
        long uid = userService.getUidFromCookie(cookie);
        System.out.printf(String.valueOf(uid));
    }


    @Test
    public void testA() {
        System.out.println("a");
    }

    @Test
    public void testB() {
        String cacheRoutePath = "/tmp/tesla/";
        String fileName = "api_route.cache";
        Utils.writeFile(cacheRoutePath, fileName, "aaa");
    }
}
