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

import lombok.Data;

import java.time.Instant;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/19 14:16
 */
@Data
public class AccessMilogParam {

    private String spaceName;
    private String storeName;
    private Long appId;
    private String appName;
    private String appCreator;
    private Long appCreatTime;
    private Long funcId;
    private String funcName;
    private String logPath;
    private Integer appType;
    private String appTypeText;
    private String envName;
    private Long envId;

    private String machineRoom = "cn";

    public static void main(String[] args) {
        System.out.println(Instant.now().toEpochMilli());
    }
}
