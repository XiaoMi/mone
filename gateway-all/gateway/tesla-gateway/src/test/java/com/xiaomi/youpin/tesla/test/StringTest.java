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

import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * @Author goodjava@qq.com
 * @Date 2021/3/24 09:33
 */
public class StringTest {


    @Test
    public void testBytes() {
        byte[] b = new byte[]{50, 32, 50, 48, 50, 48, 48, 51, 48, 57, 48, 54, 53, 57, 53, 57, 48, 49, 48, 48, 50, 55, 48,
                51, 55, 48, 49, 57, 57, 57, 52, 51, 54, 50, 58, 3, 24, -32, 93, 74, 0};
        String s = new String(b, Charset.forName("utf8"));
        System.out.println(Arrays.toString(s.getBytes()));
    }
}
