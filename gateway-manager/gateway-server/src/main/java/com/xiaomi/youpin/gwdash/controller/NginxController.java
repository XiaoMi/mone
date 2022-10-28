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
//import com.xiaomi.youpin.gwdash.bo.MachineBo;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.dao.model.HttpService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import com.xiaomi.youpin.gwdash.service.NginxService;
//import lombok.extern.slf4j.Slf4j;
//import org.nutz.dao.Cnd;
//import org.nutz.dao.Dao;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//import java.util.Map;
//
///**
// * @author dingpei
// * @author zhangzhiyong
// */
//@RestController
//@Slf4j
//public class NginxController {
//
//    @Autowired
//    private NginxService nginxService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @Autowired
//    private Dao dao;
//
//    @RequestMapping(value = "/api/nginx/new", method = RequestMethod.POST)
//    public Result<Boolean> createAccessToken(
//            HttpServletRequest request,
//            @RequestParam("serviceName") String serviceName,
//            @RequestParam("upstreamName") String upstreamName,
//            @RequestParam("configPath") String configPath,
//            @RequestParam("group") String group
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return nginxService.createNginxService(serviceName, upstreamName, configPath, group);
//    }
//
//    @RequestMapping(value = "/api/nginx/edit", method = RequestMethod.POST)
//    public Result<Boolean> editAccessToken(
//            HttpServletRequest request,
//            @RequestParam("id") long id,
//            @RequestParam("serviceName") String serviceName,
//            @RequestParam("upstreamName") String upstreamName,
//            @RequestParam("configPath") String configPath,
//            @RequestParam("group") String group
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return nginxService.editNginxService(id, serviceName, upstreamName, configPath, group);
//    }
//
//    @RequestMapping(value = "/api/nginx/del", method = RequestMethod.GET)
//    public Result<Boolean> delAccessToken(
//            HttpServletRequest request,
//            @RequestParam("id") long id
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return nginxService.delNginxService(id);
//    }
//
//    @RequestMapping(value = "/api/nginx/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getAccessToken(
//            HttpServletRequest request,
//            @RequestParam(value = "serviceName", defaultValue = "", required = false) String serviceName,
//            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
//            @RequestParam(value = "pageSize", defaultValue = "100", required = false) int pageSize
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return nginxService.getNginxServiceList(serviceName, page, pageSize);
//    }
//
//    @RequestMapping(value = "/api/nginx/machine", method = RequestMethod.GET)
//    public Result<List<MachineBo>> getNginxMachine(
//            HttpServletRequest request,
//            @RequestParam(value = "group", defaultValue = "", required = true) String group
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return nginxService.getNginxMachine(group);
//    }
//
//    /**
//     * 发布到nacos
//     * @return
//     */
//    @RequestMapping(value = "/api/nginx/deploy/nacos", method = RequestMethod.GET)
//    public Result<Boolean> deploy2Nacos(@RequestParam("id") long id) {
//        HttpService httpService = dao.fetch(HttpService.class, id);
//        if (null != httpService) {
//            nginxService.snycConfig2Nacos(httpService);
//        }
//        return Result.success(true);
//    }
//
//
//    /**
//     * 发布到nginx
//     * @return
//     */
//    @RequestMapping(value = "/api/nginx/deploy/nginx", method = RequestMethod.GET)
//    public Result<Boolean> deploy2Nginx(@RequestParam("id") long id) {
//        HttpService httpService = dao.fetch(HttpService.class, id);
//        if (null != httpService) {
//            nginxService.deployConfig2Nginx(httpService);
//        }
//
//        return Result.success(true);
//    }
//
//
//    @RequestMapping(value = "/api/nginx/upstreamNameDetail", method = RequestMethod.GET)
//    public Result<List<String>> getUpstreamNameDetail(
//            HttpServletRequest request,
//            @RequestParam(value = "ip", required = true) String ip,
//            @RequestParam(value = "configPath", required = true) String configPath,
//            @RequestParam(value = "upstreamName", required = true) String upstreamName
//    ) {
//        SessionAccount sessionAccount = loginService.getAccountFromSession(request);
//        return nginxService.getUpstreamNameDetail(ip, configPath, upstreamName);
//    }
//
//}
