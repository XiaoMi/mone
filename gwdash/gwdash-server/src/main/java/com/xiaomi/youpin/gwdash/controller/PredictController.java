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

import com.xiaomi.youpin.gwdash.bo.ReplicatesInfo;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.PredictStatusEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.PredictConfig;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.PredictService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import com.xiaomi.youpin.oracle.api.service.OracleService;
import com.xiaomi.youpin.oracle.api.service.bo.PredictResult;
import com.xiaomi.youpin.oracle.api.service.bo.QueryResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
public class PredictController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private PredictService predictService;

    @Autowired
    private ProjectService projectService;

    @Value("${ref.oracle.service.group}")
    private String oracleGroup;

    @Reference(check = false, interfaceClass = OracleService.class, group = "${ref.oracle.service.group}")
    private OracleService oracleService;

    @GetMapping("/api/predict/replicates")
    public Result<ReplicatesInfo> getReplicatesInfo(@RequestParam long projectId, @RequestParam long envId) {
        return Result.success(predictService.getReplicatesInfo(projectId, envId));
    }

    @GetMapping("/api/predict/getConfig")
    public Result<PredictConfig> getConfig(HttpServletRequest request, @RequestParam long projectId) {
        return Result.success(predictService.getConfigByProjectId(projectId));
    }

    @PostMapping("/api/predict/editConfig")
    public Result<Boolean> editConfig(HttpServletRequest request, @RequestBody PredictConfig config) {
        if (config == null || config.getProjectId() <= 0 || StringUtils.isEmpty(config.getDomain()) || StringUtils.isEmpty(config.getType()) || config.getStatus() == null || config.getQps() <= 0) {
            log.error("params error for edit predict config {}", config);
            return new Result(400, "参数有误", false);
        }
        SessionAccount account = loginService.getAccountFromSession(request);
        if (!projectService.isOwner(config.getProjectId(), account)) {
            return new Result(2, "应用owner才能操作", false);
        }

        PredictConfig oldConfig = predictService.getConfigByProjectId(config.getProjectId());
        if (oldConfig == null) {
            PredictConfig predictConfig = predictService.insert(config);
            //调用update url 更新predict server端保存的 预测type
            updateType(config.getDomain(), config.getType(), config.getQps(), config.getProjectId(), config.getStatus());
            return Result.success(true);
        }


        if (!Objects.equals(config.getType(), oldConfig.getType()) || !Objects.equals(config.getDomain(), oldConfig.getDomain())
        || oldConfig.getQps()!=config.getQps() || oldConfig.getStatus() != config.getStatus()
            || oldConfig.getProjectId()!=config.getProjectId()
        ) {
            //调用update url 更新predict server端保存的 预测type
            updateType(config.getDomain(), config.getType(), config.getQps(), config.getProjectId(), config.getStatus());
        }
        oldConfig.setDomain(config.getDomain());
        oldConfig.setType(config.getType());
        oldConfig.setStatus(config.getStatus());
        oldConfig.setQps(config.getQps());
        oldConfig.setProjectId(config.getProjectId());
        return Result.success(predictService.updateConfig(oldConfig));
    }

    /**
     * 必须update数据才能获取预测数据
     *
     * @param domain
     * @param type
     */
    private void updateType(String domain, String type, int qps, long projectId, int status) {
        oracleService.setPredictConfig(domain, type, qps, projectId, PredictStatusEnum.ON.getCode() == status);
    }

    @GetMapping("/api/predict/getRealData")
    public Result getRealData(@RequestParam long projectId) {
        PredictConfig config = predictService.getConfigByProjectId(projectId);
        if (config == null) {
            log.info("no config exist, project id : {}", projectId);
            return Result.success(null);
        }
        if (config.getStatus() != PredictStatusEnum.ON.getCode()) {
            log.info("config is off, projectId: {}", projectId);
            return Result.success(null);
        }
        LinkedList<QueryResult> realtimeData = oracleService.realtimeData(config.getDomain(), config.getType());
        return Result.success(Result.success(realtimeData));
    }

    @GetMapping("/api/predict/getPredictData")
    public Result getPredictData(@RequestParam long projectId) {
        PredictConfig config = predictService.getConfigByProjectId(projectId);
        if (config == null) {
            log.info("no config exist, project id : {}", projectId);
            return Result.success(null);
        }

        if (config.getStatus() != 0) {
            log.info("config is off, projectId: {}", projectId);
            return Result.success(null);
        }

        PredictResult predict = predictService.predict(config.getDomain(), config.getType());

        if (null != predict) {
            return Result.success(predict.getForecast());
        }
        log.info("predict result is null: {}", projectId);
        return Result.success(null);
    }

    @GetMapping("/predict/getConfigs")
    public Result<List<PredictConfig>> getConfigs() {
        return Result.success(predictService.getActiveConfigs());

    }

}
