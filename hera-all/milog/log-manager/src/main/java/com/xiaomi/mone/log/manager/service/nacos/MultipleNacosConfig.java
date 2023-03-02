package com.xiaomi.mone.log.manager.service.nacos;

import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/18 15:09
 */
@Component
@Slf4j
public class MultipleNacosConfig {

    @Value("$nacosAddr")
    private String nacosAdders;

    public static Map<String, ConfigService> nacosConfigMap = new HashMap<>();

    public void init() {
        Arrays.stream(StringUtils.split(nacosAdders, "\\$")).forEach(nacosAdder -> {
            try {
                nacosConfigMap.put(nacosAdder, ConfigFactory.createConfigService(nacosAdder));
            } catch (NacosException e) {
                log.error(String.format("multiple nacos address init error:address:%s", nacosAdder), e);
            }
        });
        log.info("multiple nacos address:{}", nacosConfigMap);
    }
}
