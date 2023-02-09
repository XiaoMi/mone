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

package com.xiaomi.data.push.crypto;


import com.xiaomi.youpin.common.crypto.Aes;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class YoupinAes {


    // 加密
    public static String encrypt(String sSrc, String sKey, String ivParameter) throws Exception {
        return Aes.encrypt(sSrc,sKey,ivParameter);
    }


    // 解密
    public static String decrypt(String sSrc, String sKey, String ivParameter) {
        return Aes.decrypt(sSrc,sKey,ivParameter);
    }


}
