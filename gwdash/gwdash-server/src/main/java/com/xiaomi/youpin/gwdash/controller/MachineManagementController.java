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

import com.xiaomi.youpin.gwdash.bo.*;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.MachineManagementServiceImp;
import com.xiaomi.youpin.gwdash.service.PipelineService;
import com.xiaomi.youpin.gwdash.service.ProjectDeploymentService;
import com.xiaomi.youpin.quota.service.QuotaService;
import com.xiaomi.youpin.tesla.agent.po.PingReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tsingfu
 */
@Slf4j
@RestController
public class MachineManagementController {

    @Autowired
    private MachineManagementServiceImp machineManagementServiceImp;

    @Reference(check = false, interfaceClass = QuotaService.class, retries = 0, group = "dev")
    private QuotaService quotaService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private ProjectDeploymentService projectDeploymentService;
    @Autowired
    private LoginService loginService;


    @RequestMapping(value = "/api/machine/list", method = RequestMethod.GET)
    public Result<Map<String, Object>> getList(
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "20") int pageSize,
        @RequestParam(value = "queryKey", required = false, defaultValue = "") String queryKey,
        @RequestParam(value = "queryValue", required = false, defaultValue = "") String queryValue,
        @RequestParam(value = "labelKey", required = false, defaultValue = "") String labelKey,
        @RequestParam(value = "labelValue", required = false, defaultValue = "") String labelValue) {
        return machineManagementServiceImp.getList(page, pageSize, queryKey, queryValue, labelKey, labelValue);
    }

    @RequestMapping(value = "/api/machine/add", method = RequestMethod.POST)
    public Result<Boolean> add(@RequestBody MachineBo machineBo) {
        return machineManagementServiceImp.add(machineBo);
    }

    @RequestMapping(value = "/api/machine/edit", method = RequestMethod.POST)
    public Result<Boolean> edit(@RequestBody MachineBo machineBo) {
        return machineManagementServiceImp.edit(machineBo);
    }

    @RequestMapping(value = "/api/machine/systemLabels", method = RequestMethod.GET)
    public Result<List<String>> getSystemLabels() {
        Field[] fields = PingReq.class.getDeclaredFields();
        List ret = Arrays.stream(fields).map(it -> {
            return it.getName();
        }).collect(Collectors.toList());
        // 额外增加的label
        ret.add("utime");
        return Result.success(ret);
    }

    @RequestMapping(value = "/api/machine/del", method = RequestMethod.POST,consumes = {"application/json"})
    public Result<Boolean> delete(@RequestBody MachineDelListVo machineDelListVo, HttpServletRequest request) {
        SessionAccount account = loginService.getAccountFromSession(request);
        log.info("user:{} try to del machine:{}",account.getUsername(),machineDelListVo.getIds());
        return machineManagementServiceImp.delete(machineDelListVo.getIds());
    }

    /**
     * 一次插入多条label
     *
     * @return
     */
    @RequestMapping(value = "/api/machine/label/update", method = RequestMethod.POST)
    public Result<Boolean> updateLabel(@RequestBody LabelsVo labelsVo) {
        return machineManagementServiceImp.updateLabel(labelsVo.getMachineId(), labelsVo);
    }

    /**
     * 插入单条label
     *
     * @param id
     * @param key
     * @param value
     * @return
     */
    @RequestMapping(value = "/api/machine/label/insert", method = RequestMethod.POST)
    public Result<Boolean> insertLabel(@RequestParam("id") long id, @RequestParam("id") String key, @RequestParam("value") String value) {
        return machineManagementServiceImp.insertLabel(id, key, value);
    }

    /**
     * 删除单条label
     *
     * @param id
     * @param key
     * @return
     */
    @RequestMapping(value = "/api/machine/label/del", method = RequestMethod.POST)
    public Result<Boolean> deleteLabel(@RequestParam("id") long id, @RequestParam("id") String key) {
        return machineManagementServiceImp.removeLabel(id, key);
    }

    /**
     * 与应用关联或解除
     */
    @RequestMapping(value = "/api/machine/application", method = RequestMethod.POST)
    public Result<Boolean> handleMachineApplication(@RequestBody ProjectEnvMachineBo projectEnvMachineBo) {
        boolean isBind = projectEnvMachineBo.isBind();
        if (isBind) {
            return machineManagementServiceImp.bindApplication(projectEnvMachineBo);
        }
        return machineManagementServiceImp.unbindApplication(projectEnvMachineBo.getId());
    }

    /**
     * 查看机器关联的所有应用
     */
    @RequestMapping(value = "/api/machine/list/app", method = RequestMethod.GET)
    public Result<List<ProjectEnvMachineBo>> listApplicationsOfMachine(@RequestParam("machineId") long machineId) {
        return machineManagementServiceImp.listApplicationsOfMachine(machineId);
    }
}
