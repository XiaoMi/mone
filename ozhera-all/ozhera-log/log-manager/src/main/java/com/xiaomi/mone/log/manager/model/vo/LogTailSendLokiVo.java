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
package com.xiaomi.mone.log.manager.model.vo;

import com.xiaomi.mone.log.model.LogtailConfig;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/5 10:47
 */
@Data
public class LogTailSendLokiVo {
    private String spaceName;
    private Long spaceId;
    private String storeName;
    private Long storeId;
    private String tailName;
    private Long tailId;
    private String keyList;

    private LogtailConfig config;

}
