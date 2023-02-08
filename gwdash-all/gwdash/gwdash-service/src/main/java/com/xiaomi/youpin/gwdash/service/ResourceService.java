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

package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.DeployInfo;
import com.xiaomi.youpin.gwdash.bo.DeploySetting;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ProjectEnv;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.quota.bo.QuotaInfo;
import com.xiaomi.youpin.quota.bo.ResourceBo;
import com.xiaomi.youpin.quota.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author dp
 */

@Slf4j
@Service
public class ResourceService {

    @Reference(group = "${ref.quota.service.group}", interfaceClass = com.xiaomi.youpin.quota.service.ResourceService.class, check = false)
    private com.xiaomi.youpin.quota.service.ResourceService quotaResourceService;

    @Autowired
    private Dao dao;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private ProjectEnvService projectEnvService;

    @Autowired
    private ProjectDeploymentService projectDeploymentService;

    @Reference(check = false, interfaceClass = QuotaService.class, retries = 0, group = "${ref.quota.service.group}")
    private QuotaService quotaService;


    public Result<Boolean> updateOrderByIp(Integer id, Integer order) {

        try {
            quotaResourceService.updateOrderById(id, order);
            return Result.success(true);
        } catch (Exception e) {
            return new Result<>(1, e.getMessage(), false);
        }
    }

    public com.xiaomi.youpin.quota.bo.Result<Map<String, Object>> getResourceList(int page, int pageSize, int status, HashMap<String, String> map) {
        return quotaResourceService.list(page, pageSize, status, map);
    }

    public com.xiaomi.youpin.quota.bo.Result<ResourceBo> getResourceByIp(String ip) throws Exception{
        return quotaResourceService.getResourceByIp(ip);
    }

    public List<Long> offline(String ip) {
        QuotaInfo quotaInfo = new QuotaInfo();
        quotaInfo.setIp(ip);
        List<ResourceBo> ret = quotaService.offline(quotaInfo).getData();
        List<Long> list = ret.stream()
                .map(item -> {
                    long envId = item.getBizId();
                    ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(envId).getData();
                    ProjectEnv projectEnv = projectEnvService.getProjectEnvById(envId).getData();
                    if (projectPipeline != null && null != projectEnv) {
                        log.info("reousrce service offline  envId:{} pipelineId:{} ip:{}", envId, projectPipeline.getId(), ip);
                        projectDeploymentService.dockerMachineOffline(projectPipeline, ip);
                        DeployInfo deployInfo = projectPipeline.getDeployInfo();
                        deployInfo.offlineDockerMachine(ip);
                        DeploySetting deploySetting = projectPipeline.getDeploySetting();
                        deploySetting.setDockerReplicate(deployInfo.getDockerMachineList().size());
                        dao.update(projectPipeline);
                        projectDeploymentService.startHealthCheck(projectEnv.getHealthCheckTaskId(), projectPipeline);
                    }
                    return item.getBizId();
                })
                .collect(Collectors.toList());
        return list;
    }

    public Result<Integer> setPrice(String ip, long price) {
        return Result.success(quotaResourceService.setPrice(ip, price).getData());
    }

}
