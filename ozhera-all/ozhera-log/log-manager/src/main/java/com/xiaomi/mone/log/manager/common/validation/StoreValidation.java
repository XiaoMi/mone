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
import com.google.common.collect.Maps;
import com.xiaomi.mone.log.api.model.vo.ResourceUserSimple;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.dao.MilogMiddlewareConfigDao;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.model.pojo.MilogEsClusterDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogMiddlewareConfig;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;
import com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionService;
import com.xiaomi.mone.log.manager.service.extension.store.StoreExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.impl.MilogMiddlewareConfigServiceImpl;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author: wtt
 * @date: 2022/5/12 17:10
 * @description: Parameter validation in the store
 */
@Slf4j
@Component
public class StoreValidation {

    @Resource
    private MilogMiddlewareConfigServiceImpl milogMiddlewareConfigService;

    private StoreExtensionService storeExtensionService;

    @Resource
    private MilogMiddlewareConfigDao milogMiddlewareConfigDao;

    @Resource
    private EsCluster esCluster;

    public void init() {
        storeExtensionService = StoreExtensionServiceFactory.getStoreExtensionService();
    }

    public String logStoreParamValid(LogStoreParam storeParam) {
        if (null == MoneUserContext.getCurrentUser()) {
            throw new MilogManageException("please go to login");
        }
        List<String> errorInfos = Lists.newArrayList();
        if (null == storeParam.getSpaceId()) {
            errorInfos.add("Space information cannot be empty");
        }
        if (null == storeParam || StringUtils.isBlank(storeParam.getLogstoreName())) {
            errorInfos.add("logStore  cannot be empty");
        }
        if (StringUtils.isEmpty(storeParam.getMachineRoom())) {
            errorInfos.add("Computer room information  cannot be empty");
            return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
        }
        if (null == storeParam.getLogType()) {
            errorInfos.add("Log type  cannot be empty");
        }
        if (StringUtils.isEmpty(storeParam.getKeyList())) {
            errorInfos.add("Index columns  cannot be empty");
        }
        List<String> duplicatedKeyList = getDuplicatedKeys(storeParam.getKeyList());
        if (!duplicatedKeyList.isEmpty()) {
            errorInfos.add("Index column fields are duplicated, remove the duplicate fields:" + duplicatedKeyList);
        }
        // Additional field inspection
        if (storeExtensionService.storeInfoCheck(storeParam)) {
            return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
        }
        if (null == storeParam.getMqResourceId() || null == storeParam.getEsResourceId()) {
            //Verify whether the department to which the current user belongs initializes the resource
            ResourceUserSimple resourceUserSimple = milogMiddlewareConfigService.userResourceList(storeParam.getMachineRoom(), storeParam.getLogType());
            if (!resourceUserSimple.getInitializedFlag()) {
                errorInfos.add(resourceUserSimple.getNotInitializedMsg());
            }
            boolean resourceChosen = null == storeParam.getMqResourceId() || null == storeParam.getEsResourceId();
            if (resourceUserSimple.getInitializedFlag() &&
                    resourceUserSimple.getShowFlag() && resourceChosen) {
                errorInfos.add("Please select the required resource information first");
            }
        } else {
            if (null != storeParam.getMqResourceId()) {
                MilogMiddlewareConfig milogMiddlewareConfig = milogMiddlewareConfigDao.queryById(storeParam.getMqResourceId());
                if (null == milogMiddlewareConfig) {
                    errorInfos.add("MQ resource information cannot be empty");
                }
            }
            if (null != storeParam.getEsResourceId()) {
                MilogEsClusterDO esClusterDO = esCluster.getById(storeParam.getEsResourceId());
                if (null == esClusterDO) {
                    errorInfos.add("ES resource information cannot be empty");
                }
            }
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }

    private List<String> getDuplicatedKeys(String keyListStr) {
        String[] keyList = keyListStr.split(",");
        List<String> duplicatedKeys = Lists.newArrayList();
        Map<String, Boolean> keyMap = Maps.newHashMap();
        for (int i = 0; i < keyList.length; i++) {
            String keyName = keyList[i].split(":")[0];
            if (keyMap.containsKey(keyName)) {
                duplicatedKeys.add(keyName);
            } else {
                keyMap.put(keyName, true);
            }
        }
        return duplicatedKeys;
    }
}
