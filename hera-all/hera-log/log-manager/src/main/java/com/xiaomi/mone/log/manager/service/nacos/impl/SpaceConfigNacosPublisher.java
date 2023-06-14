package com.xiaomi.mone.log.manager.service.nacos.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigPublisher;
import com.xiaomi.mone.log.model.MilogSpaceData;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import static com.xiaomi.mone.log.common.Constant.DEFAULT_GROUP_ID;
import static com.xiaomi.mone.log.common.Constant.TAIL_CONFIG_DATA_ID;

/**
 * @author wtt
 * @version 1.0
 * @description namespace 配置推送nacos
 * @date 2021/7/16 10:36
 */
@Slf4j
public class SpaceConfigNacosPublisher implements DynamicConfigPublisher<MilogSpaceData> {

    @Setter
    private ConfigService configService;

    @Override
    public void publish(String uniqueSpace, MilogSpaceData config) {
        log.info("写入的创建namespace配置：{}", gson.toJson(config));
        String dataId = CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + TAIL_CONFIG_DATA_ID + uniqueSpace;
        try {
            configService.publishConfig(dataId, DEFAULT_GROUP_ID, gson.toJson(config));
        } catch (NacosException e) {
            log.error(String.format("推送日志配置数据数据异常,dataId:{},data:%s", dataId, gson.toJson(config)), e);
        }
    }

    @Override
    public void remove(String spaceId) {
        String dataId = CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + TAIL_CONFIG_DATA_ID + spaceId;
        try {
            configService.removeConfig(dataId, DEFAULT_GROUP_ID);
        } catch (NacosException e) {
            log.error(String.format("删除日志配置数据数据异常,参数：%s", dataId), e);
        }
    }
}
