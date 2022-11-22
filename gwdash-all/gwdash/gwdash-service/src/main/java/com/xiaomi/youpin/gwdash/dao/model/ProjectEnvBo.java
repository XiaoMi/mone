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
 * @author zhangjunyi
 * created on 2020/7/7 10:19 上午
 */
@Data
public class ProjectEnvBo {
    private int projectId;
    private String projectName;
    private int envId;
    private String envName;
    private int deployType;
    private Double cpuNum;
    private Double cpuUsage =0D;
    private Double memoryUsage=0D;
    private List<String> owner;
    private Double dockerCount;
}