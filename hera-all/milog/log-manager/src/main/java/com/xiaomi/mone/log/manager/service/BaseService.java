package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.manager.common.context.MoneUserContext;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import com.xiaomi.mone.log.manager.model.bo.CreateOrUpdateSpaceCmd;
import com.xiaomi.mone.log.manager.model.pojo.LogSpaceDO;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/20 15:56
 */
public class BaseService {

    public void wrapMilogSpace(LogSpaceDO ms, CreateOrUpdateSpaceCmd param, String source) {
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
