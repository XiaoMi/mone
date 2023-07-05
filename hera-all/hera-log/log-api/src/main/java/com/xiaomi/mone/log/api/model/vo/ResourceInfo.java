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
package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/10 11:25
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class ResourceInfo extends CommonVo implements Serializable {
    private Long id;
    private String alias;
    private String regionEn;
    private String regionCn;
    private String serviceUrl;
    private String ak;
    private String sk;
    private String orgId;
    private String teamId;
    private String clusterName;
    private String brokerName;
    private String esToken;
    private String conWay;
    private String catalog;
    private String database;
    private List<String> labels;
    private List<EsIndexVo> multipleEsIndex;

}
