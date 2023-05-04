package com.xiaomi.mone.app.service.extension;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/24 16:59
 */
public interface AppTypeServiceExtension {
    /**
     * 日志类型转化为平台类型
     *
     * @param type
     * @return
     */
    Integer getAppTypeLog(Integer type);

    /**
     * 得到日志类型
     *
     * @param type
     * @return
     */
    Integer getAppTypePlatformType(Integer type);


    String getPlatformName(Integer platformType);

    String getAppTypeName(Integer appType);
}
