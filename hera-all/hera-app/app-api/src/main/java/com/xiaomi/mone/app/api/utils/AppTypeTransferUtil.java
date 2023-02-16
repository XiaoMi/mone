package com.xiaomi.mone.app.api.utils;

import com.xiaomi.mone.app.enums.PlatFormTypeEnum;
import com.xiaomi.mone.app.enums.ProjectTypeEnum;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 15:50
 */
public class AppTypeTransferUtil {
    private AppTypeTransferUtil() {
    }

    /**
     * 通过milog的应用类型查找对应的应用类型
     *
     * @param logTypeCode
     * @return
     */
    public static Integer queryPlatformTypeWithLogType(Integer logTypeCode) {
        Optional<ProjectTypeEnum> typeEnumOptional = Arrays.stream(ProjectTypeEnum.values())
                .filter(projectTypeEnum -> Objects.equals(logTypeCode, projectTypeEnum.getCode()))
                .findFirst();
        if (typeEnumOptional.isPresent()) {
            return typeEnumOptional.get().getPlatFormTypeCode();
        }
        return null;
    }

    /**
     * 通过milog的应用类型查找对应的应用类型
     *
     * @param logTypeCode
     * @return
     */
    public static Integer queryLogTypeWithPlatformType(Integer logTypeCode) {
        Optional<PlatFormTypeEnum> typeEnumOptional = Arrays.stream(PlatFormTypeEnum.values())
                .filter(projectTypeEnum -> Objects.equals(logTypeCode, projectTypeEnum.getCode()))
                .findFirst();
        if (typeEnumOptional.isPresent()) {
            return typeEnumOptional.get().getProjectTypeCode();
        }
        return null;
    }
}
