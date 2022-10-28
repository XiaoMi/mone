///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.controller;
//
//
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.tesla.auth.api.bo.AuthConfigList;
//import com.xiaomi.youpin.tesla.auth.api.bo.AuthConfigReq;
//import com.xiaomi.youpin.tesla.auth.api.bo.Result;
//import com.xiaomi.youpin.tesla.auth.api.service.AuthDubboService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//
///**
// * @author dingpei@xiaomi.com
// * @description: dubbo鉴权
// */
//@RestController
//@Slf4j
//@RequestMapping("/api/rpc/auth")
//public class RpcAuthController {
//
//    @Reference(check = false, interfaceClass = AuthDubboService.class, retries = 0, group = "${ref.rpc.auth.service.group}")
//    private AuthDubboService authDubboService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @RequestMapping(value = "config/getList", method = {RequestMethod.GET, RequestMethod.POST})
//    public Result<AuthConfigList> getAuthConfigList(@RequestBody AuthConfigReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            return authDubboService.getAuthConfigList(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "config/get", method = {RequestMethod.GET, RequestMethod.POST})
//    public Result<Map<String, Set<String>>> getAuthConfig(@RequestBody AuthConfigReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setOperator(account.getUsername());
//            return authDubboService.getAuthConfig(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "config/new", method = {RequestMethod.POST})
//    public Result<Boolean> newAuthConfig(@RequestBody AuthConfigReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setOperator(account.getUsername());
//            return authDubboService.newAuthConfig(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "config/del", method = {RequestMethod.POST})
//    public Result<Boolean> delAuthConfig(@RequestBody AuthConfigReq req, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.traffice.del] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            req.setOperator(account.getUsername());
//            return authDubboService.deleteAuthConfig(req);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    /**
//     * 更新
//     *
//     * @param req
//     * @param request
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    @RequestMapping(value = "config/update", method = {RequestMethod.POST})
//    public Result<Boolean> update(@RequestBody AuthConfigReq req, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.traffic.update] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            req.setOperator(account.getUsername());
//            return authDubboService.updateAuthConfig(req);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//}