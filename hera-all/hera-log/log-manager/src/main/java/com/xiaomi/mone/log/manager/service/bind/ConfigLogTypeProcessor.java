package com.xiaomi.mone.log.manager.service.bind;

import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import com.xiaomi.mone.log.common.Config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description 读取配置文件的方式判断
 * @date 2022/12/23 14:02
 */
//@Processor(isDefault = true, order = 100)
public class ConfigLogTypeProcessor implements LogTypeProcessor {

    private final Config config;

    public ConfigLogTypeProcessor(Config config) {
        this.config = config;
    }

    @Override
    public boolean supportedConsume(LogTypeEnum logTypeEnum) {
        String notConsume = config.get("log_type_mq_not_consume", "");
        List<Integer> logTypesNotConsume = Arrays.stream(notConsume.split(","))
                .map(Integer::valueOf).collect(Collectors.toList());
        return !logTypesNotConsume.contains(logTypeEnum.getType());
    }
}
