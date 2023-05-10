package com.xiaomi.mone.log.manager.service.extension.common;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/27 16:13
 */
public interface CommonExtensionService {

    String DEFAULT_COMMON_EXTENSION_SERVICE_KEY = "defaultCommonExtensionService";


    String getLogManagePrefix();

    String getHeraLogStreamServerName();

    String getMachineRoomName(String machineRoomEn);

    boolean middlewareEnumValid(Integer type);
}
