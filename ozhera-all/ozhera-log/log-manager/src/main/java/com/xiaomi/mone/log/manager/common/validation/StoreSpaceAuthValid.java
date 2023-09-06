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
import com.xiaomi.mone.log.manager.dao.MilogLogstoreDao;
import com.xiaomi.mone.log.manager.dao.MilogSpaceDao;
import com.xiaomi.mone.log.manager.model.bo.StoreSpaceAuth;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:32
 */
@Slf4j
@Component
public class StoreSpaceAuthValid {

    @Resource
    private MilogLogstoreDao milogLogstoreDao;

    @Resource
    private MilogSpaceDao milogSpaceDao;

    public String validParam(StoreSpaceAuth storeSpaceAuth) {
        List<String> errorInfos = Lists.newArrayList();
        if (null == storeSpaceAuth) {
            errorInfos.add("The parameter cannot be empty");
        }
        if (null == storeSpaceAuth.getStoreId()) {
            errorInfos.add("The store ID cannot be empty");
        }
        if (null == storeSpaceAuth.getSpaceId()) {
            errorInfos.add("The space ID cannot be empty");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }

    public String validStoreAuthData(StoreSpaceAuth storeSpaceAuth) {
        List<String> errorInfos = Lists.newArrayList();
        MilogLogStoreDO milogLogStoreDO = milogLogstoreDao.queryById(storeSpaceAuth.getStoreId());
        if (null == milogLogStoreDO) {
            errorInfos.add("The store information does not exist, please check if it is correct");
        }
        MilogSpaceDO milogSpaceDO = milogSpaceDao.queryById(storeSpaceAuth.getSpaceId());
        if (null == milogSpaceDO) {
            errorInfos.add("The space information does not exist, please check if it is correct");
        }
        if (storeSpaceAuth.getSpaceId().equals(milogLogStoreDO.getSpaceId())) {
            errorInfos.add("The store already belongs to the space and cannot be relicensed");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
