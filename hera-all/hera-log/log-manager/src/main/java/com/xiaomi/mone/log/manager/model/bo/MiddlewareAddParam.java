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

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:57
 */
@Data
public class MiddlewareAddParam implements Serializable {
    /**
     * com.xiaomi.mone.log.api.enums.MiddlewareEnum.code
     */
    private Integer type;

    private String regionEn;

    private List<?> types;

    private String alias;

    private String nameServer;

    private String serviceUrl;

    private String ak;

    private String sk;

    private String authorization;

    private String orgId;

    private String teamId;

    private Integer isDefault = 0;

}
