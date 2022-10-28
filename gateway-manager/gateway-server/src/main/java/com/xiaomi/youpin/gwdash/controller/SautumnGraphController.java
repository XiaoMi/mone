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
//import com.xiaomi.youpin.tesla.sautumn.graph.api.bo.*;
//import com.xiaomi.youpin.tesla.sautumn.graph.api.service.SautumnGraphDubboService;
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
//import java.util.Optional;
//
///**
// * @author dingpei@xiaomi.com
// * @description: 图形化编程
// */
//@RestController
//@Slf4j
//@RequestMapping("/api/sautumn/graph")
//public class SautumnGraphController {
//
//    @Autowired
//    private LoginService loginService;
//
//    @Reference(group = "${ref.sautumn.graph.group}", interfaceClass = SautumnGraphDubboService.class, check = false)
//    private SautumnGraphDubboService sautumnGraphDubboService;
//
//    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
//    public Result<SautumnGraphList> getRecordingConfigList(@RequestBody GetSautumnGraphListReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            return sautumnGraphDubboService.getSautumnGraphList(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "/new", method = {RequestMethod.POST})
//    public Result<Boolean> newRecordingConfig(@RequestBody SautumnGraph param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setCreator(account.getUsername());
//            param.setUpdater(account.getUsername());
//            return sautumnGraphDubboService.newSautumnGraphg(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//    @RequestMapping(value = "/update", method = {RequestMethod.POST})
//    public Result<Boolean> updateRecordingConfig(@RequestBody SautumnGraph param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUpdater(account.getUsername());
//            return sautumnGraphDubboService.updateSautumnGraphg(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
//    public Result<Boolean> deleteRecordingConfig(@RequestBody SautumnGraphReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUser(account.getUsername());
//            return sautumnGraphDubboService.deleteSautumnGraphg(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//    @RequestMapping(value = "/gencode", method = {RequestMethod.POST})
//    public Result<String> genCode(@RequestBody SautumnGraphReq param, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        try {
//            param.setUser(account.getUsername());
//            return sautumnGraphDubboService.genCode(param);
//        } catch (Exception e) {
//            return Result.fail(e.getMessage());
//        }
//    }
//
//
//}