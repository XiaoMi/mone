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
public class LogTailDTO {
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
     * Service deployment space
     **/
    private String deploySpace;

    /**
     * Row regex
     **/
    private String firstLineReg;

    /**
     *   china
     */
    private String source;
    /**
     * App type 0: Mione app
     **/
    private Integer appType;
    /**
     * Machine Type 0. Container 1. Physical machine
     */
    private Integer machineType;
    /**
     *  The node information of the data center where the application resides
     */
    private List<MotorRoomDTO> motorRooms;

    private String topicName;
    private List<?> middlewareConfig;

    private Integer deployWay;

    private Integer projectSource;

    private Integer batchSendSize;
}
