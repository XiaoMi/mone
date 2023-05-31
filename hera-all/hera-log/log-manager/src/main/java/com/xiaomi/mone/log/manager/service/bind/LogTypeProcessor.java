package com.xiaomi.mone.log.manager.service.bind;

import com.xiaomi.mone.log.api.enums.LogTypeEnum;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/23 14:00
 */
public interface LogTypeProcessor {
    /**
     * 是否支持消费
     *
     * @param logTypeCode
     * @return
     */
    boolean supportedConsume(Integer logTypeCode);
}
