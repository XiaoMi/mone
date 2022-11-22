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

package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Data
public class ErrorContent {

    private long pipelineId;

    private long envId;

    private long projectId;

    private List<String> ips;

    private String name;

    private long qps;

    /**
     * qps列表
     */
    private List<Long> qpsList;


    /**
     * 实例数量
     */
    private long replicate;

    /**
     * auto scaling 推荐的机器数量
     */
    private long recommendedReplicateCount;

}
