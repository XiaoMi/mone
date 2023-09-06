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
package com.xiaomi.mone.log.manager.model.bo;

import com.xiaomi.mone.log.manager.model.dto.MotorRoomDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogTailParam {
    private Long id;
    private Long spaceId;
    private Long storeId;
    private Long milogAppId;
    private Long appId;
    private String appName;
    private String deploySpace;
    private Long envId;
    private String envName;
    private List<String> ips;
    private String tail;
    private Integer parseType;
    private String parseScript;
    private String logPath;
    private String valueList;
    private String tailRate;
    private Long ctime;
    private Long utime;
    private Long middlewareConfigId;

    private String logSplitExpress;
    /**
     * Beginning of line rule
     */
    private String firstLineReg;
    /**
     * Application type 0:mione application
     **/
    private Integer appType;
    /**
     * Machine type 0.Container 1.Physical machine
     */
    private Integer machineType;
    /**
     * The node information of the computer room where the mis application is located
     */
    private List<MotorRoomDTO> motorRooms;

    private String topicName;
    private List<?> middlewareConfig;
    /**
     * Deployment method 1-mione;
     */
    private Integer deployWay;

    private Integer batchSendSize = 20;
}
