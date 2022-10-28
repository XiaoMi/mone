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
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
///**
// * @author tsingfu
// */
//@Controller
//@RequestMapping(value = "/", method = RequestMethod.GET)
//@Slf4j
//public class PageController {
//
//    @Value("${server.serverEnv}")
//    private String serverEnv;
//
//    @Autowired
//    private LoginService loginService;
//
//    @RequestMapping("/404")
//    public String notFoundHtml() {
//        return "404.html";
//    }
//
//    @RequestMapping("/error")
//    public String errorHtml() {
//        return "500.html";
//    }
//
//    @RequestMapping("/403")
//    public String forbiddenHtml() {
//        return "403.html";
//    }
//
//    @RequestMapping("/gwdash/**")
//    public String restHtml(HttpServletRequest request, HttpServletResponse response, ModelMap map) throws IOException {
//        SessionAccount sessionAccount = loginService.getAccountAndResourceFromSession(request);
//        if (null == sessionAccount) {
//            response.sendRedirect("/403");
//            return null;
//        }
//        map.put("user", sessionAccount);
//        map.put("serverEnv", serverEnv);
//        return "index.html";
//    }
//}
