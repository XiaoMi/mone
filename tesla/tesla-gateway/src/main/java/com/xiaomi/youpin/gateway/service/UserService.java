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

package com.xiaomi.youpin.gateway.service;

import com.xiaomi.youpin.account.passportproxy.PassportProxyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

/**
 * @author dingpei
 */
@Slf4j
@Service
public class UserService {

    @Reference(check = false, interfaceClass = PassportProxyService.class, timeout = 600, retries = 1)
    private PassportProxyService passportProxyService;

    /**
     * cookie 转换uid
     *
     * @throws TException
     */
    public long getUidFromCookie(String cookie) throws TException {
        try {
            if (cookie.equals("")) {
                return 0L;
            }

            com.xiaomi.youpin.account.passportproxy.api.ParseCookieRequest request = new com.xiaomi.youpin.account.passportproxy.api.ParseCookieRequest();
            request.setCookie(cookie);

            com.xiaomi.youpin.account.passportproxy.api.ParseCookieResponse res = passportProxyService.ParseCookie(request);
            return res.getUserId();

        } catch (Throwable ex) {
            log.warn("user service error:" + ex.getMessage(), ex);
            return 0L;
        }
    }

}
