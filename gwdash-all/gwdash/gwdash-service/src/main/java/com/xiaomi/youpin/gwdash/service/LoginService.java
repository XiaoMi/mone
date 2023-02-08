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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.hermes.bo.ResourceBo;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.AccountRegisterRequest;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.xiaomi.youpin.gwdash.common.Consts.*;

@Service
public class LoginService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    @Value("${server.serverName}")
    private String serverName;

    @Value("${server.serverEnv}")
    private String serverEnv;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private UserService userService;

    public void validate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Assertion assertion = (Assertion) request.getSession(false).getAttribute("_const_cas_assertion_");
        String username = assertion.getPrincipal().getName();
        if (StringUtils.isBlank(username)) {
            LOGGER.warn("[LoginService.login] failed to login by CAS, username: {}", username);
            request.getSession(false).invalidate();
        }

        response.sendRedirect(serverName);
    }

    public SessionAccount getAccountFromSession(HttpServletRequest request) {
        if (null == request) {
            return null;
        }

        HttpSession session = request.getSession(false);
        if (null == session) {
            return null;
        }

        Assertion assertion = (Assertion) session.getAttribute("_const_cas_assertion_");
        String username = assertion.getPrincipal().getName();
        if (StringUtils.isBlank(username)) {
            request.getSession(false).invalidate();
            return null;
        }

        Account account = userService.queryUserByName(username);
        if (null == account) {
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            if (attributes != null && attributes.size() > 0) {
                try {
                    String cname = attributes.get("name").toString();
                    if (Objects.equals(cname, "null")) {
                        cname = attributes.get("displayName").toString();
                    }
                    String email = attributes.get("email").toString();
                    AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest();
                    accountRegisterRequest.setProjectName(projectName);
                    accountRegisterRequest.setUsername(username);
                    accountRegisterRequest.setCname(URLDecoder.decode(cname, "utf-8"));
                    accountRegisterRequest.setEmail(URLDecoder.decode(email, "utf-8"));
                    account = userService.registerAccount(accountRegisterRequest);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage());
                }
            } else {
                LOGGER.error("cas attributes error!");
            }
        }

        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(username);
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        int role;
        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
            role = ROLE_ADMIN;
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
            role = ROLE_WORK;
        } else {
            role = ROLE_GUEST;
        }
//        List<ResourceBo> resources = userService.getUserResource(username,projectName);
        if (account == null) {
            return null;
        }
        return new SessionAccount(account.getId(), username, account.getName(), account.getToken(), role, account.getGid(), roles, account.getGidInfos());
    }

    public SessionAccount getAccountAndResourceFromSession(HttpServletRequest request) {
        if (null == request) {
            return null;
        }

        HttpSession session = request.getSession(false);
        if (null == session) {
            return null;
        }

        Assertion assertion = (Assertion) session.getAttribute("_const_cas_assertion_");
        String username = assertion.getPrincipal().getName();
        SessionAccount account = this.getAccountFromSession(request);
        List<ResourceBo> resources = userService.getUserResource(username, projectName);
        account.setResource(resources);
        return account;
    }
}
