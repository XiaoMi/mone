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
package com.xiaomi.mone.log.manager.common.validation;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description Resource management parameter verification
 * @date 2022/5/11 16:51
 */
@Slf4j
@Component
public class ResourceValidation {

    public String resourceOperateValid(MiLogResource miLogResource) {
        List<String> errorInfos = Lists.newArrayList();
        if (null == miLogResource.getOperateCode()) {
            errorInfos.add("operateCode cannot be empty");
        }
        boolean operateBoolean = Objects.equals(OperateEnum.UPDATE_OPERATE.getCode(), miLogResource.getOperateCode()) ||
                Objects.equals(OperateEnum.DELETE_OPERATE.getCode(), miLogResource.getOperateCode());
        if (operateBoolean && null == miLogResource.getId()) {
            errorInfos.add("id cannot be empty");
        }

        if (null == miLogResource.getResourceCode()) {
            errorInfos.add("resourceCode cannot be empty");
        }

        if (null != miLogResource.getResourceCode() &&
                null != miLogResource.getOperateCode() &&
                OperateEnum.DELETE_OPERATE.getCode().equals(miLogResource.getOperateCode())) {
            return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
        }

        if (MiddlewareEnum.ELASTICSEARCH.getCode().equals(miLogResource.getResourceCode())) {
            if (StringUtils.isBlank(miLogResource.getConWay())) {
                errorInfos.add("conWay  cannot be empty");
            }
            if (StringUtils.isBlank(miLogResource.getClusterName())) {
                errorInfos.add("clusterName  cannot be empty");
            }
            if (Objects.equals(ES_CONWAY_PWD, miLogResource.getConWay()) &&
                    (StringUtils.isBlank(miLogResource.getAk()) || StringUtils.isBlank(miLogResource.getSk()))) {
//                errorInfos.add("If the connection mode is pwd, the user name and password cannot be empty");
            }
            if (Objects.equals(ES_CONWAY_TOKEN, miLogResource.getConWay()) &&
                    (StringUtils.isBlank(miLogResource.getEsToken()) || StringUtils.isBlank(miLogResource.getCatalog()) ||
                            StringUtils.isBlank(miLogResource.getDatabase()))) {
                errorInfos.add("If the connection mode is token, the token, catalog cluster, and database name cannot be empty");
            }
        }
        if (StringUtils.isBlank(miLogResource.getAlias())) {
            errorInfos.add("alias cannot be empty");
        }
        if (StringUtils.isBlank(miLogResource.getRegionEn())) {
            errorInfos.add("Region code cannot be empty");
        }
        boolean esIndexExist = MiddlewareEnum.ELASTICSEARCH.getCode().equals(miLogResource.getResourceCode()) &&
                CollectionUtils.isEmpty(miLogResource.getMultipleEsIndex());
        if (esIndexExist) {
            errorInfos.add("ES index information cannot be empty");
        }
        if(MiddlewareEnum.ROCKETMQ.getCode().equals(miLogResource.getResourceCode()) &&
                StringUtils.isEmpty(miLogResource.getClusterName())){
            errorInfos.add("MQ address information cannot be empty");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
