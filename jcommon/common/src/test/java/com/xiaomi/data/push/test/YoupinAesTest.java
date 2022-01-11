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

package com.xiaomi.data.push.test;

import com.xiaomi.data.push.crypto.YoupinAes;
import org.junit.Test;

public class YoupinAesTest {


    @Test
    public void testEncrypt() throws Exception {
        String s = YoupinAes.encrypt("zzy", "B31F2A75FBF94099", "1234567890123456");
        System.out.println(s);
        System.out.println(YoupinAes.decrypt(s,"B31F2A75FBF94099","1234567890123456"));


        System.out.println(YoupinAes.decrypt("liD2hnSjroQdpW1otjd6HPzdwJ986dKLI9UjDk7aKSIIWSuqLz2xqWfL2XD4HeVD5Ed/wo2/nz27u9gr25UA0r5cwVBzaVPXZz0RwelL5HF6xZh/dIy5TTl4UfbvmL/0mNK8kIlmWk8ouI5Y4YqUiQ==","9jiw092qhiu9owf9","0102030405060708"));
    }
}
