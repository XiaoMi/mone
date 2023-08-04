/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.api.model.meta;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author shanwb
 * @date 2021-07-08
 */
@Data
public class LogCollectMeta implements Serializable {
    /**
     * agent标识
     */
    private String agentId;
    /**
     * agent机器名
     */
    private String agentMachine;
    /**
     * agent机器物理机ip
     */
    private String agentIp;
    /**
     * agent收集的应用元数据
     */
    private List<AppLogMeta> appLogMetaList;

    private AgentDefine agentDefine;
    /**
     * 如果是k8s的时候，当前node的所有pod集合,专门用来处理opentelemetry日志，由于配置的*，会扫描到很多已经结束的
     * pod的日志文件，导致起很多线程而线程池满了后放入不了任务的问题
     * <p>
     * 启动时为当前机器上全量挂载了日志的podName
     * 运行过程中为删除的pod
     */
    private List<String> podNames;

    /**
     * 单个配置数据，默认该机器下的全量配置
     */
    private Boolean singleMetaData;

    private String podType;

    /**
     * 某个机器下线的时候需要删除的该目录下的日志采集,只有当某个应用的机器下线时才有值
     */
    private String delDirectory;

}
