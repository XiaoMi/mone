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
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.ProjectJavaDoc;
//import com.xiaomi.youpin.gwdash.service.JavaDocService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.ProjectService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//
//@RestController
//@Slf4j
//public class JavaDocController {
//
//    @Autowired
//    private JavaDocService javaDocService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @RequestMapping(value = "/api/javadoc/get", method = RequestMethod.GET)
//    public Result<ProjectJavaDoc> javaDoc(@RequestParam("projectId") long projectId) {
//        ProjectJavaDoc javaDoc = javaDocService.getJavaDoc(projectId);
//        return Result.success(javaDoc);
//    }
//
//    @RequestMapping(value = "/api/javadoc/create")
//    public Result createJavaDoc(
//            HttpServletRequest request,
//            @RequestParam("projectId") long projectId) {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (projectService.isMember(projectId, account)) {
//            Result result = javaDocService.createJavaDoc(projectId, account);
//            return Result.success(result);
//        }
//        return new Result(1, "需项目成员才可操作", null);
//    }
//}
