package com.xiaomi.mone.log.manager.service.nacos.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigPublisher;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/15 15:14
 */
@Slf4j
public class StreamConfigNacosPublisher implements DynamicConfigPublisher<MiLogStreamConfig> {

    @Setter
    private ConfigService configService;

    @Override
    public synchronized void publish(String app, MiLogStreamConfig config) {
        if (config == null) {
            return;
        }
        try {
            configService.publishConfig(LOG_MANAGE_PREFIX + NAMESPACE_CONFIG_DATA_ID, DEFAULT_GROUP_ID, gson.toJson(config));
        } catch (NacosException e) {
            log.error(String.format("创建namespace推送数据异常,参数：%s", gson.toJson(config)), e);
        }
    }

    @Override
    public void remove(String app) {

    }

}
