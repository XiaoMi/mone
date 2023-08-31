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
package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import com.xiaomi.mone.log.manager.model.MilogSpaceParam;
import com.xiaomi.mone.log.manager.model.pojo.MilogSpaceDO;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/20 15:56
 */
public class BaseService {

    public void wrapMilogSpace(MilogSpaceDO ms, MilogSpaceParam param) {
        ms.setSpaceName(param.getSpaceName());
        ms.setTenantId(param.getTenantId());
        ms.setSource(MoneUserContext.getCurrentUser().getZone());
        ms.setDescription(param.getDescription());
    }

    public void wrapMilogSpace(MilogSpaceDO ms, MilogSpaceParam param, String source) {
        ms.setSpaceName(param.getSpaceName());
        ms.setTenantId(param.getTenantId());
        ms.setSource(source);
        ms.setDescription(param.getDescription());
    }

    public void wrapBaseCommon(BaseCommon common, OperateEnum operateEnum) {
        if (operateEnum == OperateEnum.ADD_OPERATE) {
            common.setCtime(System.currentTimeMillis());
            common.setCreator(MoneUserContext.getCurrentUser().getUser());
        }
        common.setUtime(System.currentTimeMillis());
        common.setUpdater(MoneUserContext.getCurrentUser().getUser());
    }

    public void wrapBaseCommon(BaseCommon common, OperateEnum operateEnum, String appCreator) {
        if (operateEnum == OperateEnum.ADD_OPERATE) {
            common.setCtime(System.currentTimeMillis());
            common.setCreator(appCreator);
        }
        common.setUtime(System.currentTimeMillis());
        common.setUpdater(appCreator);
    }
}
