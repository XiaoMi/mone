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

package com.xiaomi.youpin.gwdash.controller;

import com.xiaomi.mone.tpc.common.vo.NodeVo;
import com.xiaomi.mone.tpc.common.vo.PageDataVo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.bo.TenementParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.config.EnvConfig;
import com.xiaomi.youpin.gwdash.service.GroupServiceApiRpc;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.UserService;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
public class TenementController {

    @Autowired
    private LoginService loginService;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private UserService userService;

    @Value("${skip.midun:false}")
    private boolean skipMiDun;

    @Autowired
    private EnvConfig envConfig;

    @Autowired
    private GroupServiceApiRpc groupServiceAPI;

    @Autowired
    private TenantComponent tenantComponent;


    /**
     * 获取租户map信息
     *
     * @return
     */
    @SneakyThrows
    @RequestMapping(value = "/tenement", method = RequestMethod.POST, consumes = {"application/json"})
    public Result getTenementList(HttpServletResponse response) {
        SessionAccount account = getSessionAccount();
        if (null == account) {
            log.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        String name = account.getUsername();
        return Result.success(tenantComponent.getTenementList(name));
    }

    /**
     * 更新用户选中的租户
     *
     * @param param
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/tenement/set", method = RequestMethod.POST, consumes = {"application/json"})
    public Result setTenement(@RequestBody TenementParam param, HttpServletResponse response) throws IOException {
        SessionAccount account = getSessionAccount();
        if (null == account) {
            log.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        tenantComponent.setTenantToMetadata(account, param);
        return Result.success("ok");
    }

    /**
     * 获取tenement
     *
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/tenement/get", method = RequestMethod.POST, consumes = {"application/json"})
    public Result getTenement(HttpServletResponse response) throws IOException {
        SessionAccount account = getSessionAccount();
        String name = account.getUsername();
        String tenant = tenantComponent.getTenantFromMetadata(name);
        log.info("tenant:{}", tenant);
        if (StringUtils.isEmpty(tenant)) {
            tenant = "1";
//            PageDataVo<NodeVo> data = tenantComponent.getTenantInfo("system");
//            tenant = String.valueOf(data.getList().get(0).getOutId());
        }
        return Result.success(tenant);
    }

    private SessionAccount getSessionAccount() {
        SessionAccount account = loginService.getAccountFromSession();
        log.info("getSessionAccount account : [{}]", account);
        return account;
    }

}
