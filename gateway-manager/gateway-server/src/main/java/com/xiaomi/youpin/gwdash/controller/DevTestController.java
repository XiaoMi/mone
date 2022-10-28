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
//import com.mysql.jdbc.StringUtils;
//import com.xiaomi.youpin.gwdash.bo.DubboTestBo;
//import com.xiaomi.youpin.gwdash.bo.HttpTestBo;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.service.DevTestService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.mibench.bo.RequestParam;
//import com.xiaomi.youpin.mibench.service.OpenApiService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author zhangjunyi
// * created on 2020/4/27 3:51 下午
// */
//@RestController
//@Slf4j
//public class DevTestController {
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private DevTestService devTestService;
//
//    @Reference(check = false, interfaceClass = OpenApiService.class, group = "")
//    private OpenApiService openApiService;
//
//    @RequestMapping(value = "/open/api/devTest/dubboTest",method = RequestMethod.POST)
//    public Result<Object> dubboTestOpen(
//            @RequestBody DubboTestBo params,
//            HttpServletRequest request,
//            HttpServletResponse response
//    ) throws IOException {
//        if (StringUtils.isNullOrEmpty(request.getParameter("userName"))){
//            response.sendError(401,"userName为空");
//            return null;
//        }
//        if(!"78A9C1304B29B806".equalsIgnoreCase(request.getHeader("token"))){
//            response.sendError(401,"token校验失败");
//            return null;
//        }
//        log.info("dubboTestOpen user:{},params:{}",request.getParameter("userName"),params);
//        Result<Object> ret = devTestService.excecuteDubbo(params);
//        return Result.success(ret.isSuccess()?ret.getData():ret.getMessage());
//    }
//
//    @RequestMapping(value = "/api/devTest/dubboTest",method = RequestMethod.POST)
//    public Result<Object> dubboTest(
//            @RequestBody DubboTestBo params,
//            HttpServletRequest request,
//            HttpServletResponse response
//            ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if(StringUtils.isNullOrEmpty(account.getName())){
//            response.sendError(401,"未登录或者无权限");
//            return null;
//        }
//        log.info("dubboTest user:{},params:{}",account.getUsername(),params);
//        Result<Object> ret = devTestService.excecuteDubbo(params);
//        return Result.success(ret.isSuccess()?ret.getData():ret.getMessage());
//    }
//
//    @RequestMapping(value = "/api/devTest/httpTest", method = RequestMethod.POST)
//    public Result<Object> httpTest(
//            @RequestBody HttpTestBo httpTestBo,
//            HttpServletRequest request,
//            HttpServletResponse response
//            ) throws IOException {
//
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if(StringUtils.isNullOrEmpty(account.getName())){
//            response.sendError(401,"未登录或者无权限");
//            return null;
//        }
//
//        RequestParam requestParam = new RequestParam();
//        requestParam.setUrl(httpTestBo.getUrl());
//        requestParam.setBody(httpTestBo.getBody());
//        requestParam.setMethod(httpTestBo.getMethod());
//        requestParam.setHeaders(httpTestBo.getHeaders());
//        requestParam.setTimeout(httpTestBo.getTimeout());
//
//        return Result.success(openApiService.httpTest(requestParam));
//    }
//}