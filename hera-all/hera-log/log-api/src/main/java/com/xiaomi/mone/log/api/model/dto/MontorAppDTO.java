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
package com.xiaomi.mone.log.api.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/8 14:27
 */
@Data
public class MontorAppDTO implements Serializable {
    private Long appId;
    private String appName;
    private String source;
    private Boolean isAccess = false;
}
