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
package com.xiaomi.mone.log.api.model.bo;

import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/10 17:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiLogResource implements Serializable {
    private Long id;
    private Integer operateCode;
    private Integer resourceCode;
    private String alias;
    private String clusterName;
    private String regionEn;
    private String conWay;
    private String serviceUrl;
    private String ak;
    private String sk;
    private String brokerName;
    private String orgId;
    private String teamId;
    private Integer isDefault;
    private String esToken;
    private String catalog;
    private String database;
    private List<String> labels = new ArrayList<>(0);
    private List<EsIndexVo> multipleEsIndex = new ArrayList<>();
}
