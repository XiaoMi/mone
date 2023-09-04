package com.xiaomi.mone.app.service.extension.impl;

import com.xiaomi.mone.app.enums.PlatFormTypeEnum;
import com.xiaomi.mone.app.enums.ProjectTypeEnum;
import com.xiaomi.mone.app.service.extension.AppTypeServiceExtension;
import com.xiaomi.mone.app.util.AppTypeTransferUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/24 17:03
 */
@Service
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class DefaultAppTypeServiceExtension implements AppTypeServiceExtension {
    @Override
    public Integer getAppTypeLog(Integer type) {
        return null;
    }

    @Override
    public Integer getAppPlatForm(Integer type) {
        return AppTypeTransferUtil.queryPlatformTypeWithLogType(type);
    }

    @Override
    public Integer getAppTypePlatformType(Integer type) {
        return AppTypeTransferUtil.queryLogTypeWithPlatformType(type);
    }

    @Override
    public String getPlatformName(Integer platformType) {
        return PlatFormTypeEnum.getEnum(platformType).getName();
    }

    @Override
    public String getAppTypeName(Integer appType) {
        return ProjectTypeEnum.queryTypeByCode(appType);
    }
}
