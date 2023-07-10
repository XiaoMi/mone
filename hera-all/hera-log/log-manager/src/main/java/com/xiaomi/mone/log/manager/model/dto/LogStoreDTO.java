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

import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/17 11:38
 */
@Data
public class LogStoreDTO extends MilogLogStoreDO {
    /**
     * 日志类型名称中文
     */
    private String logTypeName;
    /**
     * 机房名称中文
     */
    private String machineRoomName;
    /**
     * 是否选择自定义索引
     */
    private Boolean selectCustomIndex;

    private Long esResourceId ;

}
