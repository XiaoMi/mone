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

import com.xiaomi.youpin.gwdash.bo.GroupInfoEntity;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.TenantUtils;
import com.xiaomi.youpin.gwdash.config.EnvConfig;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static com.xiaomi.youpin.gwdash.common.Consts.*;

@Slf4j
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

    @Value("${skip.midun:false}")
    private boolean skipMiDun;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private TenantUtils tenantUtils;

    public SessionAccount getAccountFromSession() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getAccountFromSession(request);
    }

    @Deprecated
    public SessionAccount getAccountFromSession(HttpServletRequest request, boolean skipMiDun) {
        return getRootAccount();
//        if (null == request) {
//            return null;
//        }
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user && !skipMiDun) {
//            return null;
//        }
//        String username = user != null ? user.getUser() : request.getHeader(SKIP_MI_DUN_USER_NAME);
//
//        // 将用户注册到hermes
//        Account account = userService.queryUserByName(username);
//        if (null == account) {
//            if (user == null) {
//                return null;
//            }
//            try {
//                String cname = user.getName();
//                if (Objects.equals(cname, "null")) {
//                    cname = user.getDisplayName();
//                }
//                String email = user.getEmail();
//                AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest();
//                accountRegisterRequest.setProjectName(projectName);
//                accountRegisterRequest.setUsername(username);
//                accountRegisterRequest.setCname(URLDecoder.decode(cname, "utf-8"));
//                accountRegisterRequest.setEmail(URLDecoder.decode(email, "utf-8"));
//                account = userService.registerAccount(accountRegisterRequest);
//
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage());
//            }
//        }
//
//        // 将用户注册到网关用户表
//        GWAccount gwAccount = null;
//        String tenant = tenantUtils.getTenant(request);
//        gwAccount = userService.queryGWUserByName(username, tenant);
//        if (null == gwAccount) {
//            try {
//                gwAccount = userService.registerAccount(username, "", tenant);
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage());
//            }
//        }
//
//        log.info("getAccountFromSession account:[{}], gwAccount:[{}]", account, gwAccount);
//
//        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
//        queryRoleRequest.setProjectName(projectName);
//        queryRoleRequest.setUserName(username);
//        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
//        log.debug("getAccountFromSession queryRoleRequest:[{}] , roles: [{}]", new Gson().toJson(queryRoleRequest), new Gson().toJson(roles));
//        int role;
//        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
//            role = ROLE_ADMIN;
//        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
//            role = ROLE_WORK;
//        } else {
//            role = ROLE_GUEST;
//        }
//
//        return new SessionAccount(
//                account.getId(), username, account.getName(), account.getToken(), role,
//                gwAccount.getGid(), roles,
//                gwAccount.getGidInfos());
    }

    /**
     * 可直接使用
     *
     * @param request
     * @return
     * @see LoginService#getAccountFromSession()
     */
    @Deprecated
    public SessionAccount getAccountFromSession(HttpServletRequest request) {
        return getRootAccount();
//        if (null == request) {
//            return null;
//        }
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user && !skipMiDun) {
//            return null;
//        }
//        String username = user != null ? user.getUser() : request.getHeader(SKIP_MI_DUN_USER_NAME);
//
//        // 将用户注册到hermes
//        Account account = userService.queryUserByName(username);
//        if (null == account) {
//            if (user == null) {
//                return null;
//            }
//            try {
//                String cname = user.getName();
//                if (Objects.equals(cname, "null")) {
//                    cname = user.getDisplayName();
//                }
//                String email = user.getEmail();
//                AccountRegisterRequest accountRegisterRequest = new AccountRegisterRequest();
//                accountRegisterRequest.setProjectName(projectName);
//                accountRegisterRequest.setUsername(username);
//                accountRegisterRequest.setCname(URLDecoder.decode(cname, "utf-8"));
//                accountRegisterRequest.setEmail(URLDecoder.decode(email, "utf-8"));
//                account = userService.registerAccount(accountRegisterRequest);
//
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage());
//            }
//        }
//
//        // 将用户注册到网关用户表
//        GWAccount gwAccount = null;
//        String tenant = tenantUtils.getTenant(request);
//        gwAccount = userService.queryGWUserByName(username, tenant);
//        if (null == gwAccount) {
//            try {
//                gwAccount = userService.registerAccount(username, "", tenant);
//            } catch (Exception e) {
//                LOGGER.error(e.getMessage());
//            }
//        }
//
//        log.info("getAccountFromSession account:[{}], gwAccount:[{}]", account, gwAccount);
//
//        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
//        queryRoleRequest.setProjectName(projectName);
//        queryRoleRequest.setUserName(username);
//        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
//        log.debug("getAccountFromSession queryRoleRequest:[{}] , roles: [{}]", new Gson().toJson(queryRoleRequest), new Gson().toJson(roles));
//        int role;
//        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
//            role = ROLE_ADMIN;
//        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
//            role = ROLE_WORK;
//        } else {
//            role = ROLE_GUEST;
//        }
//
//        return new SessionAccount(
//                account.getId(), username, account.getName(), account.getToken(), role,
//                gwAccount.getGid(), roles,
//                gwAccount.getGidInfos());
    }

    public SessionAccount getAccountAndResourceFromSession(HttpServletRequest request) {
        return getRootAccount();

//        if (null == request) {
//            return null;
//        }
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user) {
//            return null;
//        }
//        String username = user.getUser();
//        SessionAccount account = this.getAccountFromSession(request);
//        List<ResourceBo> resources = userService.getUserResource(username, projectName);
//        account.setResource(resources);
//        return account;
    }

    public List<RoleBo> getAllRoles() {
        return userService.getAllRolesByProjectName(projectName);
    }

    //数据库初始化一条gw_group_info数据
    private SessionAccount getRootAccount(){
        RoleBo rb = new RoleBo();
        rb.setId(1L);
        rb.setName("admin");
        GroupInfoEntity groupInfoEntity = new GroupInfoEntity();
        groupInfoEntity.setId(1);
        groupInfoEntity.setName("测试");
        List<RoleBo> roles = Arrays.asList(rb);
        List<GroupInfoEntity> gidInfos = Arrays.asList(groupInfoEntity);
        return new SessionAccount(1L, "root", "root", "token", ROLE_ADMIN,"1", roles, gidInfos);
    }
}
