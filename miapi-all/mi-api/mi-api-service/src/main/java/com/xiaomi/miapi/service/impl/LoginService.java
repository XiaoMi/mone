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

package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.util.SessionAccount;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
public class LoginService {

    public SessionAccount getAccountFromSession(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        String username;
        AuthUserVo user = UserUtil.getUser();
        if (null == user) {
            username = "Tony";
        }else {
            username = user.getAccount();
        }
        return new SessionAccount(9999L, username,username, null, null, null, null, null);
    }
}
