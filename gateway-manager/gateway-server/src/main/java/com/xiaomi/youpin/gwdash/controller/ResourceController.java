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
//import com.xiaomi.youpin.gwdash.bo.RemoveBo;
//import com.xiaomi.youpin.gwdash.bo.ResourceParam;
//import com.xiaomi.youpin.gwdash.bo.SessionAccount;
//import com.xiaomi.youpin.gwdash.common.Consts;
//import com.xiaomi.youpin.gwdash.common.Result;
//import com.xiaomi.youpin.gwdash.exception.CommonError;
//import com.xiaomi.youpin.gwdash.service.*;
//import com.xiaomi.youpin.quota.bo.ResourceBo;
//import com.xiaomi.youpin.quota.service.QuotaService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.Reference;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
///**
// * @author dp
// */
//@RestController
//@Slf4j
//public class ResourceController {
//
//    @Autowired
//    private ResourceService resourceService;
//
//    @Autowired
//    private LoginService loginService;
//
//    @Reference(check = false, interfaceClass = QuotaService.class, retries = 0, group = "${ref.quota.service.group}", timeout = 3000)
//    private QuotaService quotaService;
//
//    @Autowired
//    private ProjectService projectService;
//
//    @Autowired
//    private PipelineService pipelineService;
//
//    @Autowired
//    private ProjectDeploymentService projectDeploymentService;
//
//    @Autowired
//    private ProjectEnvService projectEnvService;
//
//    /**
//     * 展示 resource 列表
//     **/
//    @RequestMapping(value = "/api/resource/list", method = RequestMethod.GET)
//    public Result<Map<String, Object>> getResourceList(HttpServletRequest request,
//                                                       HttpServletResponse response,
//                                                       @RequestParam(required = false, value = "status", defaultValue = "-1") int status,
//                                                       @RequestParam(required = false, value = "ip", defaultValue = "") String ip,
//                                                       @RequestParam(required = false, value = "owner", defaultValue = "") String owner,
//                                                       @RequestParam(required = false, value = "tenement", defaultValue = "") String tenement,
//                                                       @RequestParam("page") int page,
//                                                       @RequestParam("pageSize") int pageSize) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//
//        HashMap<String, String> map = new HashMap<>();
//        if (ip != null) {
//            map.put("ip", ip);
//        }
//        if (owner != null) {
//            map.put("owner", owner);
//        }
//        try {
//            com.xiaomi.youpin.quota.bo.Result<Map<String, Object>> result = resourceService.getResourceList(page, pageSize, status,tenement,map);
//            if (result.getCode() == 0) {
//                return Result.success(result.getData());
//            }
//            return new Result(1, result.getMessage(), false);
//        } catch (Exception e) {
//            return new Result(1, e.getMessage(), false);
//        }
//    }
//
//    /**
//     *  获取CPU、Mem的总量和已使用量
//     **/
//    @RequestMapping(value = "/api/resource/getTotalAndUesdCpuAndMemValues", method = RequestMethod.GET)
//    public Result<Map<String,Map<String, Long>>> getTotalAndUesdCpuAndMemValues(HttpServletRequest request,
//                                                       HttpServletResponse response
//                                                       ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getTotalAndUesdCpuAndMemValues] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            com.xiaomi.youpin.quota.bo.Result<Map<String,Map<String, Long>>> result = resourceService.getTotalAndUesdCpuAndMemValues();
//            if (result.getCode() == 0) {
//                return Result.success(result.getData());
//            }
//            return new Result(1, result.getMessage(), false);
//        } catch (Exception e) {
//            return new Result(1, e.getMessage(), false);
//        }
//    }
//
//    /**
//     *  获取当前可选租户列表
//     **/
//    @RequestMapping(value = "/api/resource/getAllTenement", method = RequestMethod.GET)
//    public Result<List<Map<String,String>>> getAllTenement(HttpServletRequest request,
//                                                                    HttpServletResponse response
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getAllTenement] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        List<Map<String,String>> result = resourceService.getAllTenement();
//        return Result.success(result);
//    }
//
//    /**
//     *  获取当前租户限制
//     **/
//    @RequestMapping(value = "/api/resource/getTenementLimit", method = RequestMethod.GET)
//    public Result<Map<String,Double>> getTenementLimit(HttpServletRequest request,
//                                               HttpServletResponse response,
//                                                 @RequestParam("tenement") String tenementName
//
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getAllTenement] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        Result<Map<String,Double>> result = resourceService.getTenementLimit(tenementName);
//
//        return result;
//    }
//
//    /**
//     * 更新 resource (update)
//     */
//    @RequestMapping(value = "/api/resource/update", method = RequestMethod.POST)
//    public Result<Boolean> updateResource(
//            @RequestBody ResourceParam param,
//        HttpServletRequest request,
//        HttpServletResponse response
//        ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.updateResourceImage] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
//            log.warn("[ResourceController.deleteResource] not authorized to operate group");
//            return Result.fail(CommonError.NotAuthorizedGroupOptError);
//        }
//
//        return resourceService.updateResourceByIp(param);
//    }
//
//    /**
//     * 删除 resource (delete)
//     */
//    @RequestMapping(value = "/api/resource/delete", method = RequestMethod.POST)
//    public Result<Boolean> deleteResource(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            @RequestBody RemoveBo ipBo
//            ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.deleteResource] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        if (account.getRole().intValue() != Consts.ROLE_ADMIN) {
//            log.warn("[ResourceController.deleteResource] not authorized to operate group");
//            return Result.fail(CommonError.NotAuthorizedGroupOptError);
//        }
//        return resourceService.deleteResourceByIp(ipBo.getIp());
//    }
//
//    @RequestMapping(value = "/api/resource/updateTenementLimit", method = RequestMethod.POST)
//    public Result<Boolean> updateTenementLimit(
//                                               @RequestParam("tenement") String tenement,
//                                               @RequestParam("cpu_limit") double cpuLimit,
//                                               @RequestParam("mem_limit") double memLimit
//    ) throws IOException {
//        if (cpuLimit >= 1.0 || cpuLimit <= 0 || memLimit >= 1.0 || memLimit <= 0){
//            return Result.fail(CommonError.InvalidParamError);
//        }
//        return resourceService.updateTenement(tenement,cpuLimit,memLimit);
//    }
//
//    @RequestMapping(value = "/api/resource/offline", method = RequestMethod.POST)
//    public List<Long> machineOffline(@RequestParam("ip") String ip) {
//        return resourceService.offline(ip);
//    }
//
//    @RequestMapping(value = "/api/resource/getByIp", method = RequestMethod.POST)
//    public Result<ResourceBo> getResourceByIp(
//        HttpServletRequest request,
//        HttpServletResponse response,
//        @RequestParam("ip") String ip
//    ) throws IOException {
//        SessionAccount account = loginService.getAccountFromSession(request);
//        if (!Optional.ofNullable(account).isPresent()) {
//            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
//            response.sendError(401, "未登录或者无权限");
//            return null;
//        }
//        try {
//            com.xiaomi.youpin.quota.bo.Result<ResourceBo> result = resourceService.getResourceByIp(ip);
//            if (result.getCode() == 0) {
//                return Result.success(result.getData());
//            }
//            return new Result(1, result.getMessage(), false);
//        } catch (Exception e) {
//            return new Result(1, e.getMessage(), false);
//        }
//
//    }
//
//    @RequestMapping(value = "/api/resource/price/set", method = RequestMethod.POST)
//    public Result<Integer> setPrice(
//        HttpServletRequest request,
//        HttpServletResponse response,
//        @RequestParam("ip") String ip,
//        @RequestParam("price") long price
//    ) {
//        return resourceService.setPrice(ip, price);
//    }
//
//    @RequestMapping(value = "/api/resource/startserver", method = RequestMethod.GET)
//    public Result<Boolean> startServer(@RequestParam("ip") String ip) {
//        quotaService.startServer(ip);
//        return Result.success(true);
//    }
//
//    @RequestMapping(value = "/api/resource/stopserver", method = RequestMethod.GET)
//    public Result<Boolean> stopServer(@RequestParam("ip") String ip) {
//        quotaService.stopServer(ip);
//        return Result.success(true);
//    }
//}
