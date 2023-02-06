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
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.umami.Umami;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.AccountRegisterRequest;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.List;

@Service
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private UserService userService;

    public SessionAccount getAccountFromSession(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        AuthUserVo user = UserUtil.getUser();
        if (null == user) {
            return null;
        }
        String username = user.getAccount();
        Account account = userService.queryUserByName(username);
        if (null == account) {
            try {
                //todo 自动注册用户信息，可去除，使用tpc用户名为唯一键
                String cname = user.getName();
                String email = user.getEmail();
                AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest();
                accountRegisterRequest.setProjectName(Consts.PROJECT_NAME);
                accountRegisterRequest.setUsername(username);
                accountRegisterRequest.setCname(URLDecoder.decode(cname, "utf-8"));
                accountRegisterRequest.setEmail(URLDecoder.decode(email, "utf-8"));
                account = userService.registerAccount(accountRegisterRequest);
            } catch (Exception e) {
                LOGGER.error(e.getMessage());
            }
        }

        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(Consts.PROJECT_NAME);
        queryRoleRequest.setUserName(username);
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        int role;
        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
            role = Consts.ROLE_ADMIN;
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
            role = Consts.ROLE_WORK;
        } else {
            userService.assginWorkRoleForMiApi(account.getId());
            role = Consts.ROLE_WORK;
        }

        return new SessionAccount(account.getId(), username, account.getName(), account.getToken(), role, account.getGid(), roles, account.getGidInfos());
    }

}
