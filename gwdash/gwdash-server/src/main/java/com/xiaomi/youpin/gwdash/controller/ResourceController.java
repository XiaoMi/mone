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

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.*;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author dp
 */
@RestController
@Slf4j
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private LoginService loginService;

    @Reference(check = false, interfaceClass = QuotaService.class, retries = 0, group = "${ref.quota.service.group}")
    private QuotaService quotaService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private ProjectDeploymentService projectDeploymentService;

    @Autowired
    private ProjectEnvService projectEnvService;

    /**
     * 展示 resource 列表
     **/
    @RequestMapping(value = "/api/resource/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getResourceList(HttpServletRequest request,
                                                       HttpServletResponse response,
                                                       @RequestParam(required = false, value = "status", defaultValue = "0") int status,
                                                       @RequestParam(required = false, value = "ip", defaultValue = "") String ip,
                                                       @RequestParam(required = false, value = "owner", defaultValue = "") String owner,
                                                       @RequestParam("page") int page,
                                                       @RequestParam("pageSize") int pageSize) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }

        HashMap<String, String> map = new HashMap<>();
        if (ip != null) {
            map.put("ip", ip);
        }
        if (owner != null) {
            map.put("owner", owner);
        }

        try {
            com.xiaomi.youpin.quota.bo.Result<Map<String, Object>> result = resourceService.getResourceList(page, pageSize, status, map);
            if (result.getCode() == 0) {
                return Result.success(result.getData());
            }
            return new Result(1, result.getMessage(), false);
        } catch (Exception e) {
            return new Result(1, e.getMessage(), false);
        }
    }

    /**
     * 更新 resource (update)
     */
    @RequestMapping(value = "/api/resource/update", method = RequestMethod.POST)
    public Result<Boolean> updateResourceImage(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("id") int id,
            @RequestParam("order") int order
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        return resourceService.updateOrderByIp(id, order);
    }

    @RequestMapping(value = "/api/resource/offline", method = RequestMethod.POST)
    public List<Long> machineOffline(@RequestParam("ip") String ip) {
        return resourceService.offline(ip);
    }

    @RequestMapping(value = "/api/resource/getByIp", method = RequestMethod.POST)
    public Result<ResourceBo> getResourceByIp(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("ip") String ip
    ) throws IOException {
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!Optional.ofNullable(account).isPresent()) {
            log.warn("[ResourceController.getResourceList] current user not have valid account info in session");
            response.sendError(401, "未登录或者无权限");
            return null;
        }
        try {
            com.xiaomi.youpin.quota.bo.Result<ResourceBo> result = resourceService.getResourceByIp(ip);
            if (result.getCode() == 0) {
                return Result.success(result.getData());
            }
            return new Result(1, result.getMessage(), false);
        } catch (Exception e) {
            return new Result(1, e.getMessage(), false);
        }

    }

    @RequestMapping(value = "/api/resource/price/set", method = RequestMethod.POST)
    public Result<Integer> setPrice(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam("ip") String ip,
            @RequestParam("price") long price
    ) {
        return resourceService.setPrice(ip, price);
    }
}
