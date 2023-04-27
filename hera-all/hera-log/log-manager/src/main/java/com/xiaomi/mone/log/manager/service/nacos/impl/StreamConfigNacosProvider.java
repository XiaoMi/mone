package com.xiaomi.mone.log.manager.service.nacos.impl;

import com.alibaba.nacos.api.config.ConfigService;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import com.xiaomi.mone.log.manager.service.nacos.DynamicConfigProvider;
import com.xiaomi.mone.log.model.MiLogStreamConfig;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static com.xiaomi.mone.log.common.Constant.*;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/16 15:27
 */
@Slf4j
public class StreamConfigNacosProvider implements DynamicConfigProvider<MiLogStreamConfig> {

    @Setter
    private ConfigService configService;

    @Override
    public MiLogStreamConfig getConfig(String appName) {
        String rules = null;
        try {
            rules = configService.getConfig(CommonExtensionServiceFactory.getCommonExtensionService().getLogManagePrefix() + NAMESPACE_CONFIG_DATA_ID, DEFAULT_GROUP_ID, DEFAULT_TIME_OUT_MS);
            log.info("nacos中查询日志最初配置：{}", rules);
            if (StringUtils.isNotEmpty(rules)) {
                return gson.fromJson(rules, MiLogStreamConfig.class);
            }
        } catch (Exception e) {
            log.error(String.format("查询命名空间配置数据数据异常,参数：%s", rules), e);
        }
        return null;
    }
}
