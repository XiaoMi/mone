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

package com.xiaomi.youpin.tesla.file.server.service;

import com.xiaomi.youpin.common.crypto.Aes;
import com.xiaomi.youpin.tesla.file.server.common.Config;
import com.xiaomi.youpin.tesla.file.server.common.Cons;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class TokenService {

    public boolean check(String str, String token, String type) {
        if (type.equals(Cons.UPLOAD) || type.equals(Cons.GETTOKEN)) {
            return token.equals(Cons.TOKEN);
        }
        if (type.equals(Cons.DOWNLOAD)) {
            return token.equals(Cons.TOKEN);
//            return generateToken(str).equals(token);
        }
        return true;
    }

    public String generateToken(String str) {
        try {
            return Aes.encrypt(str, Config.ins().get("tokenKey", ""), Config.ins().get("tokenIvParam", ""));
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
        return "";
    }


    public String decryptToken(String token) {
        return Aes.decrypt(token, Config.ins().get("tokenKey", ""), Config.ins().get("tokenIvParam", ""));
    }


}
