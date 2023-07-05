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
package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MilogTailDTO {
    private Long id;
    private Long ctime;
    private Long utime;
    private Long spaceId;
    private Long storeId;
    private Long appId;
    private Long milogAppId;
    private String appName;
    private Long envId;
    private String envName;
    private List<String> ips;
    private String tail;
    private Integer parseType;
    private String parseScript;
    private String logPath;
    private String logSplitExpress;
    private String valueList;
    private String tailRate;
    /**
     * 服务 部署空间
     **/
    private String deploySpace;

    /**
     * 行首正则
     **/
    private String firstLineReg;

    /**
     *   china
     */
    private String source;
    /**
     * 应用类型 0:mione应用
     **/
    private Integer appType;
    /**
     * 机器类型 0.容器 1.物理机
     */
    private Integer machineType;
    /**
     *  应用所在的机房 节点信息
     */
    private List<MotorRoomDTO> motorRooms;

    private String topicName;
    private List<?> middlewareConfig;

    private Integer deployWay;

    private Integer projectSource;

    private Integer batchSendSize;
}
