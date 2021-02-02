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

import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.ApiGroupInfoService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.UserService;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

import static com.xiaomi.youpin.gwdash.common.Consts.*;

@RestController
public class ApiGroupInfoController {

    @Autowired
    private LoginService loginService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGroupInfoController.class);

    @Autowired
    private ApiGroupInfoService groupService;

    @Value("${hermes.project.name}")
    private String projectName;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/api/apigroup/new", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> newApiGroupInfo(@RequestBody ApiGroupInfoParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.debug("[ApiGroupInfoController.newApiGroupInfo] param: {}", param);

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.newApiGroupInfo] not authorized to operate group");
            return Result.fail(CommonError.NotAuthorizedGroupOptError);
        }

        return groupService.newApiGroupInfo(param);
    }

    @RequestMapping(value = "/api/apigroup/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<ApiGroupInfoListResult> getApiGroupList(@RequestBody ListParam param) {

        LOGGER.debug("[ApiGroupInfoController.getApiGroupList] param: {}", param);
        if (param == null) {
            return new Result<>(CommonError.InvalidPageParamError.code, CommonError.InvalidPageParamError.message);
        }

        if (param.getPageNo() <= 0) {
            param.setPageNo(1);
        }

        if (param.getPageSize() <= 0) {
            param.setPageSize(Consts.DEFAULT_PAGE_SIZE);
        }

        return groupService.getApiGroupList(param.getPageNo(), param.getPageSize());
    }

    @RequestMapping(value = "/api/apigroup/listall", method = RequestMethod.GET)
    public Result<ApiGroupInfoListResult> getApiGroupListAll(HttpServletRequest request, HttpServletResponse response) throws IOException {

        HttpSession session = request.getSession(false);
        if (null == session) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        Assertion assertion = (Assertion) session.getAttribute("_const_cas_assertion_");
        String username = assertion.getPrincipal().getName();
        if (StringUtils.isBlank(username)) {
            request.getSession(false).invalidate();
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(username);
        //获取分组信息
        SessionAccount account = loginService.getAccountFromSession(request);
        //获取角色信息
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("admin")).findAny().orElse(null) != null) {
            //admin
            return groupService.getApiGroupListAll(null);
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().contains("work")).findAny().orElse(null) != null) {
            //work
           return groupService.getApiGroupListAll(account.getGidInfos());
        } else {
            //guest
            return groupService.getApiGroupListAll(account.getGidInfos());
        }

    }

    @RequestMapping(value = "/api/apigroup/del", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Integer> delApiGroup(@RequestBody IDsParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {

        LOGGER.debug("[ApiGroupInfoController.delApiGroup] param: {}", param);

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.delApiGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.delApiGroup] not authorized to operate group");
            return Result.fail(CommonError.NotAuthorizedGroupOptError);
        }

        return groupService.delApiGroup(param);
    }

    @RequestMapping(value = "/api/apigroup/update", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> updateApiGroup(@RequestBody ApiGroupInfoUpdateParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.debug("[ApiGroupInfoController.updateApiGroup] param: {}", param);

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            LOGGER.warn("[ApiGroupInfoController.updateApiGroup] not authorized to operate group");
            return Result.fail(CommonError.NotAuthorizedGroupOptError);
        }

        return groupService.updateApiGroup(param);
    }

}
