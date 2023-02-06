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

import com.xiaomi.youpin.gwdash.bo.AccountDetailResult;
import com.xiaomi.youpin.gwdash.bo.AccountParam;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.bo.getAccountByUrlParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.AccountPO;
import com.xiaomi.youpin.gwdash.dao.model.ApiGroupInfo;
import com.xiaomi.youpin.gwdash.dao.model.ApiInfo;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.exception.CommonException;
import com.xiaomi.youpin.gwdash.service.ApiGroupInfoService;
import com.xiaomi.youpin.gwdash.service.ApiInfoService;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.UserService;
import com.xiaomi.youpin.hermes.bo.ResourceBo;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.validation.Assertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.youpin.gwdash.common.Consts.*;

@RestController
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private ApiInfoService apiInfoService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;
    @Autowired
    private ApiGroupInfoService apiGroupInfoService;

    @Value("${hermes.project.name}")
    private String projectName;

    /*
    @RequestMapping(value = "/api/account/new", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> newAccount(@RequestBody AccountParam param,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        LOGGER.info("[AccountController.newAccount] param: {}", param);

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.newAccount] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            return new Result<>(CommonError.UnAuthorized.code, CommonError.UnAuthorized.message);
        }

        CheckResult checkResult = BizUtils.chkNewAccountParam(param);
        if (!checkResult.isValid()) {
            return new Result<>(checkResult.getCode(), checkResult.getMsg());
        }

        boolean success = accountService.newAccount(param);
        if (!success) {
            return new Result<>(CommonError.NewAccountError.code, CommonError.NewAccountError.message);
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message);

    }

    @RequestMapping(value = "/api/account/update", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> updateAccount(@RequestBody AccountParam param,
                                      HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        LOGGER.info("[AccountController.updateAccount] param: {}", param);

        CheckResult checkResult = BizUtils.chkUpdateAccountParam(param);
        if (!checkResult.isValid()) {
            return new Result<>(checkResult.getCode(), checkResult.getMsg());
        }

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.updateAccount] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN &&
                account.getId().intValue() != param.getId().intValue()) {
            return new Result<>(CommonError.UnAuthorized.code, CommonError.UnAuthorized.message);
        }

        boolean success = accountService.updateAccount(param);
        if (!success) {
            return new Result<>(CommonError.NewAccountError.code, CommonError.NewAccountError.message);
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message);
    }

    @RequestMapping(value = "/api/account/del", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<Void> delAccount(@RequestBody IDParam param, HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.delAccount] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            return new Result<>(CommonError.UnAuthorized.code, CommonError.UnAuthorized.message);
        }

        if (null == param || param.getId() <= 0) {
            return new Result<>(CommonError.InvalidIDParamError.code, CommonError.InvalidIDParamError.message);
        }

        boolean success = accountService.delAccount(param.getId());
        if (!success) {
            return new Result<>(CommonError.DelAccountError.code, CommonError.DelAccountError.message);
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message);
    }
    */


    @RequestMapping(value = "/api/account/own", method = RequestMethod.GET)
    public Result<AccountDetailResult> getOwnAccountDetail(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (null == request) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }

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

        Account account = userService.queryUserByName(username);
        if (null == account) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(username);
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        AccountDetailResult ret = new AccountDetailResult();
        BeanUtils.copyProperties(account, ret);

        List<ApiGroupInfo> apiGroupInfos = apiGroupInfoService.getGidInfosByIds(ret.getGid());
        List<ResourceBo> resourceBos = userService.getUserResource(username,projectName);
        ret.setResources(resourceBos);

        //        ApiGroupInfo apiGroupInfo = apiGroupInfoService.queryGroupById(gids);
//        if (apiGroupInfo != null) {
//            ret.setGroupName(apiGroupInfo.getName());
//        }
        int role;
        if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().equals("admin")).findAny().orElse(null) != null) {
            role = ROLE_ADMIN;
        } else if (roles != null && roles.size() > 0 && roles.parallelStream().filter(e -> e.getName().equals("work")).findAny().orElse(null) != null) {
            role = ROLE_WORK;
        } else {
            role = ROLE_GUEST;
        }

        ret.setRole(role);
        ret.setUserName(username);
        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }

    /*
    @RequestMapping(value = "/api/account/detail", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<AccountDetailResult> getAccountDetail(@RequestBody IDParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (null == param || param.getId() <= 0) {
            return new Result<>(CommonError.InvalidIDParamError.code, CommonError.InvalidIDParamError.message);
        }

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getAccountDetail] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            return new Result<>(CommonError.UnAuthorized.code, CommonError.UnAuthorized.message);
        }

        AccountDetailResult ret = accountService.getAccountById(account.getId());

        if (null == ret) {
            return new Result<>(CommonError.AccountNotFoundError.code, CommonError.AccountNotFoundError.message);
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }

    @RequestMapping(value = "/api/account/list", method = RequestMethod.POST, consumes = {"application/json"})
    public Result<AccountListResult> getAccountList(@RequestBody PageParam param,
                                                    HttpServletRequest request,
                                                    HttpServletResponse response) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getAccountList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
            return new Result<>(CommonError.UnAuthorized.code, CommonError.UnAuthorized.message);
        }

        if (param == null) {
            return new Result<>(CommonError.InvalidPageParamError.code, CommonError.InvalidPageParamError.message);
        }

        if (param.getPageNo() <= 0) {
            param.setPageNo(1);
        }

        if (param.getPageSize() <= 0) {
            param.setPageSize(Consts.DEFAULT_PAGE_SIZE);
        }

        AccountListResult ret = accountService.getAccountList(param.getPageNo(), param.getPageSize());

        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }

    */

    @RequestMapping(value = "/api/account/all/list", method = RequestMethod.GET)
    public Result<List<AccountPO>> getAllAccountList(HttpServletRequest request,
                                                     HttpServletResponse response) throws IOException {

        SessionAccount account = loginService.getAccountFromSession(request);

        if (null == account) {
            LOGGER.warn("[AccountController.getLimitedAccountList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        List<Account> accounts = userService.getAllAccountList();
        if (accounts == null || accounts.size() == 0) {
            return new Result<>(CommonError.Success.code, CommonError.Success.message, null);
        }
        List<AccountPO> ret = new ArrayList<>(accounts.size());
        for (Account e : accounts) {
            AccountPO accountPO = new AccountPO();
            BeanUtils.copyProperties(e, accountPO);
            ret.add(accountPO);
        }

        return new Result<>(CommonError.Success.code, CommonError.Success.message, ret);
    }

    @RequestMapping(value = "/api/account/token", method = RequestMethod.POST)
    public Result<String> token(@RequestBody AccountParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);

        if (!account.getId().equals(param.getId())) {
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        String token = userService.generateToken(param.getId());

        return Result.success(token);
    }

    /***
     * 2019-07-23 09:21:33
     * 新增根据Url获取创建人信息
     * @author 
     * @param param
     * {
     *  url:String,
     *  username:String,
     *  token:String
     * }
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/open/account/getAccountByUrl", method = RequestMethod.POST)
    public Result getAccountByUrl(@RequestBody getAccountByUrlParam param, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.info("[AccountController.getAccountByUrl] param: {}", param);

        if (!StringUtils.isNotEmpty(param.getUsername())) {
            return new Result<>(CommonError.InvalidParamError.code, CommonError.InvalidParamError.message);
        }

        if (!StringUtils.isNotEmpty(param.getToken())) {
            LOGGER.info("[AccountController.getAccountByUrl] getToken: {}", param.getToken());
            return new Result<>(CommonError.InvalidParamError.code, CommonError.InvalidParamError.message);
        }
        Account account = userService.queryUserByName(param.getUsername());
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            throw new CommonException(CommonError.NotAuthorizedPluginOptError);
        }
        if (!StringUtils.isNotEmpty(param.getUrl())) {
            return new Result<>(CommonError.InvalidParamError.code, CommonError.InvalidParamError.message);
        }

        List<ApiInfo> list = apiInfoService.getApiInfoDetailByUrl(param.getUrl().trim());
        if (list.size() == 0) {
            LOGGER.warn("[AccountController.getAccountByUrl] list: {}", list);
            return new Result<>(CommonError.Success.code, CommonError.Success.message);
        }
        String creator = list.get(0).getCreator();
        if (!StringUtils.isNotEmpty(creator)) {
            LOGGER.warn("[AccountController.getAccountByUrl] creator: {}", creator);
            return new Result<>(CommonError.Success.code, CommonError.Success.message);

        }
        Account user = userService.queryUserByName(creator);
        AccountDetailResult userInfo = new AccountDetailResult();
        if (user != null) {
            BeanUtils.copyProperties(user, user);
        }
        return new Result<>(CommonError.Success.code, CommonError.Success.message, userInfo);
    }


}
