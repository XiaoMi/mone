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
//import com.alibaba.nacos.api.exception.NacosException;
//import com.alibaba.nacos.api.naming.pojo.Instance;
//import com.xiaomi.aegis.utils.AegisFacade;
//import com.xiaomi.aegis.vo.UserInfoVO;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.bo.NacosConfig;
//import com.xiaomi.youpin.gwdash.dao.model.NacosInstance;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.gwdash.service.NacosService;
//import com.xiaomi.youpin.gwdash.service.UserService;
//import com.xiaomi.youpin.hermes.bo.RoleBo;
//import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.UnsupportedEncodingException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
///**
// * @author gaoyibo
// */
//@RestController
//@Slf4j
//public class NacosController {
//    @Autowired
//    private NacosService nacosService;
//
//    @Autowired
//    private UserService userService;
//
//    @Value("${hermes.project.name}")
//    private String projectName;
//
//    @RequestMapping(value = "/api/nacos/service/list", method = RequestMethod.GET)
//    public Result<String> getServiceList(@RequestParam("keyword") String keyword,
//                                         @RequestParam(value = "namespaceId", required = false) String namespaceId) {
//        String allServiceList = nacosService.getAllServiceList(keyword,namespaceId);
//
//        return Result.success(allServiceList);
//    }
//
//    @RequestMapping(value = "/api/nacos/instances/detail", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<List<Instance>> getInstancesList(@RequestBody NacosInstance nacosInstance) {
//        List<Instance> list = new ArrayList<>();
//        try {
//            list = nacosService.getInstances(nacosInstance.getServiceName());
//        } catch (NacosException e) {
//            //
//        }
//
//        return Result.success(list);
//    }
//
//    @RequestMapping(value = "/api/nacos/configs/list", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<String> getConfigsList(@RequestBody NacosConfig nacosConfig) {
//        String configs = "";
//        try {
//            configs = nacosService.getConfig(nacosConfig.getDataId(), nacosConfig.getGroup(), nacosConfig.getNamespaceId(), 500L);
//        } catch (NacosException e) {
//            //
//        }
//
//        return Result.success(configs);
//    }
//
//    @RequestMapping(value = "/api/nacos/configs/update", method = RequestMethod.POST, consumes = {"application/json"})
//    public Result<String> updateConfig(HttpServletRequest request, HttpServletResponse response, @RequestBody NacosConfig nacosConfig) {
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user) {
//            return null;
//        }
//        String username = user.getUser();
//        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
//        queryRoleRequest.setProjectName(projectName);
//        queryRoleRequest.setUserName(username);
//        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
//        List<String> canEditRoleNames = Arrays.asList(Consts.canEditNacosConfigRoles);
//        boolean cantEditNacos = roles.stream().noneMatch(role ->canEditRoleNames.contains(role.getName()));
//        if (cantEditNacos) {
//            return com.xiaomi.youpin.gwdash.common.Result.fail(CommonError.UnAuthorized);
//        }
//        try {
//            return Result.success(nacosService.publishConfig(nacosConfig.getDataId(), nacosConfig.getGroup(), nacosConfig.getNamespaceId(), nacosConfig.getContent()));
//        } catch (NacosException | UnsupportedEncodingException e) {
//            log.error("updateConfig error",e);
//        }
//        return Result.fail(CommonError.UnknownError);
//    }
//}
