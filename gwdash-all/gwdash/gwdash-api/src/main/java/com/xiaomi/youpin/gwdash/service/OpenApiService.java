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

import com.xiaomi.youpin.gwdash.bo.MachineBo;
import com.xiaomi.youpin.gwdash.bo.PredictConfigBo;
import com.xiaomi.youpin.gwdash.bo.openApi.*;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.oracle.api.service.bo.PredictResult;

import java.util.List;
import java.util.Map;

/**
 * @Author lyc
 */
public interface OpenApiService {


    /**
     * 获取流量预测的项目配置
     */
    Result<List<PredictConfigBo>> getConfigs();

    /**
     * 根据项目名称或者项目id获取环境列表id （envId）
     */
    public Result<List<ProjectEnvBo>> envList(ProjectEnvRequest request);

    /**
     * docker 根据envId获取机器列表
     */
    public Result<Map<String, Object>> envMachines(ProjectEnvRequest request);

    /**
     * docker nuke
     */
    public Result<Boolean> nuke(DockerOptRequest request);


    /**
     * docker shutdown
     */
    public Result<Boolean> shutdown(DockerOptRequest request);

    /**
     * docker online
     */
    public Result<Boolean> online(DockerOptRequest request);

    /**
     * docker 扩容 缩容
     */
    public Result<Boolean> scale(DockerOptRequest request);


    /**
     * docker 机器打标签
     */
    public Result<Boolean> tag(DockerTagRequest request);

    /**
     * 设置流量预测
     *
     * @param domain        项目名称
     * @param predictResult 　流量预测object
     * @return 返回Result object
     */
    void setPredictData(String domain, PredictResult predictResult);

    /**
     * 返回项目环境的当前机器数量，最低数量，最高数量
     * @param request 需要传envId
     * @return ReplicateBo(三个integer)
     */
    Result<ReplicateBo> getReplicateInfo(ProjectEnvRequest request);

}
