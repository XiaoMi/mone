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
import com.xiaomi.youpin.gwdash.bo.PluginData;
import com.xiaomi.youpin.gwdash.common.Consts;
import com.xiaomi.youpin.gwdash.dao.model.GwStatistics;
import com.xiaomi.youpin.gwdash.dao.model.ProjectPipeline;
import com.xiaomi.youpin.infra.rpc.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  @description: gw对外提供dubbo接口
 *  @author zhenghao
 *
 */
@Slf4j
@Service(interfaceClass = GwdashApiService.class, retries = 0, group = "${dubbo.group}")
public class GwdashApiServiceImpl implements GwdashApiService {

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private Dao dao;

    @Override
    public Result<List<String>> getPhysicalIpByEnvId(long envId) {
        log.info("getPhysicalIpByEnvId envId:{},", envId);
        List<String> list = new ArrayList<>();
        ProjectPipeline projectPipeline = pipelineService.getProjectPipelineOfEnv(Long.valueOf(envId)).getData();

        DeployInfo deployInfo = projectPipeline.getDeployInfo();
        if (deployInfo != null) {
            deployInfo.getDeployBatches().stream().forEach(it -> {
                it.getDeployMachineList().forEach(machine -> {
                    list.add(machine.getIp());
                });
            });
        }
        return Result.success(list);
    }

    /**
     * 每日计算doccker数量，可以重复执行，补数据也可以执行
     * @return
     */
    @Override
    public Result<Object> dockerCalculation() {
        List<ProjectPipeline> projectPipelineList = dao.query(ProjectPipeline.class, Cnd.where("1", "=", "1").andNot("deploy_info", "is not", null).orderBy("id", "desc"));

        log.info("GwdashApiServiceImpl dockerCalculation projectPipelineList size:{}", projectPipelineList.size());
        Map<Long, Integer> map = new HashMap<>();
        AtomicReference<Integer> count = new AtomicReference<>(0);
        projectPipelineList.forEach(it -> {
            if (map.get(it.getEnvId()) == null) {
                if (it.getDeployInfo() != null) {
                    if (it.getDeployInfo().getDockerMachineList() != null) {
                        map.put(it.getEnvId(), it.getDeployInfo().getDockerMachineList().size());
                        count.updateAndGet(v -> v + it.getDeployInfo().getDockerMachineList().size());
                    }

                }
            }
        });

        log.info("GwdashApiServiceImpl dockerCalculation projectPipelineList count:{}", count);

        GwStatistics gwStatistics = dao.fetch(GwStatistics.class, Cnd.where("gw_key", "=", Consts.DOCKER_COUNT_KEY));
        if (gwStatistics == null) {
            GwStatistics gwStatisticsInsert = new GwStatistics();
            gwStatisticsInsert.setCtime(System.currentTimeMillis());
            gwStatisticsInsert.setKey(Consts.DOCKER_COUNT_KEY);
            gwStatisticsInsert.setValue(String.valueOf(count));
            dao.insert(gwStatisticsInsert);
        } else {
            dao.update(GwStatistics.class, Chain.make("gw_value", count).add("ctime", System.currentTimeMillis()), Cnd.where("gw_key", "=", Consts.DOCKER_COUNT_KEY));
        }
        return Result.success(true);
    }
}
