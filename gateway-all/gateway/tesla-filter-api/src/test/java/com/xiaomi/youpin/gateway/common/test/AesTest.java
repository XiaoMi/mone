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

package com.xiaomi.youpin.gateway.common.test;

import com.xiaomi.youpin.gateway.common.Aes;
import org.junit.Test;

public class AesTest {


    @Test
    public void testAes() {
        String text = Aes.Encrypt("abc你好哈", "1234567891234567");
        System.out.println(text);
        System.out.println(Aes.Decrypt(text,"1234567891234567"));
    }

}
