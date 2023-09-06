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
package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.common.validation.StoreSpaceAuthValid;
import com.xiaomi.mone.log.manager.dao.MilogStoreSpaceAuthDao;
import com.xiaomi.mone.log.manager.model.bo.StoreSpaceAuth;
import com.xiaomi.mone.log.manager.model.pojo.MilogStoreSpaceAuth;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.MilogStoreSpaceAuthService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:13
 */
@Slf4j
@Service
public class MilogStoreSpaceAuthServiceImpl extends BaseService implements MilogStoreSpaceAuthService {

    @Resource
    private MilogStoreSpaceAuthDao milogStoreSpaceAuthDao;

    @Resource
    private StoreSpaceAuthValid storeSpaceAuthValid;

    @Override
    public String storeSpaceAuth(StoreSpaceAuth storeSpaceAuth) {
        // Verify parameters
        String paramsErrorInfos = storeSpaceAuthValid.validParam(storeSpaceAuth);
        if (StringUtils.isNotBlank(paramsErrorInfos)) {
            throw new MilogManageException(paramsErrorInfos);
        }
        // Verify that the data is real
        String dataCollectInfos = storeSpaceAuthValid.validStoreAuthData(storeSpaceAuth);
        if (StringUtils.isNotBlank(dataCollectInfos)) {
            throw new MilogManageException(dataCollectInfos);
        }
        // Whether there is, there are modifications, there are no additions
        MilogStoreSpaceAuth milogStoreSpaceAuth = milogStoreSpaceAuthDao.queryByStoreSpace(storeSpaceAuth.getStoreId(), storeSpaceAuth.getSpaceId());
        if (null == milogStoreSpaceAuth) {
            MilogStoreSpaceAuth auth = buildStoreSpaceAuth(storeSpaceAuth.getStoreId(), storeSpaceAuth.getSpaceId());
            milogStoreSpaceAuthDao.add(auth);
            return Constant.SUCCESS_MESSAGE;
        }
        wrapBaseCommon(milogStoreSpaceAuth, OperateEnum.UPDATE_OPERATE);
        milogStoreSpaceAuthDao.update(milogStoreSpaceAuth);
        return Constant.SUCCESS_MESSAGE;
    }

    private MilogStoreSpaceAuth buildStoreSpaceAuth(Long storeId, Long spaceId) {
        MilogStoreSpaceAuth storeSpaceAuth = new MilogStoreSpaceAuth();
        storeSpaceAuth.setStoreId(storeId);
        storeSpaceAuth.setSpaceId(spaceId);
        wrapBaseCommon(storeSpaceAuth, OperateEnum.ADD_OPERATE);
        return storeSpaceAuth;
    }
}
