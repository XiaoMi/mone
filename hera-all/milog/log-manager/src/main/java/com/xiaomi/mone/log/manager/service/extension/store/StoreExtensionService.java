package com.xiaomi.mone.log.manager.service.extension.store;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.mone.log.manager.model.vo.LogStoreParam;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/4/10 16:13
 */
public interface StoreExtensionService {

    String DEFAULT_STORE_EXTENSION_SERVICE_KEY = "defaultStoreExtensionService";

    /**
     * resource bind
     *
     * @param ml
     * @param cmd
     * @param operateEnum
     */
    void storeResourceBinding(MilogLogStoreDO ml, LogStoreParam cmd, OperateEnum operateEnum);

    /**
     * Additional post processing
     *
     * @param ml
     * @param cmd
     */
    void postProcessing(MilogLogStoreDO ml, LogStoreParam cmd);

    /**
     * Send Configuration Switch
     *
     * @return
     */
    boolean sendConfigSwitch(LogStoreParam param);


}
