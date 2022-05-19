package com.xiaomi.youpin.docean.plugin.config;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.util.Optional;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@DOceanPlugin(order = 0)
@Slf4j
public class ConfigPlugin implements IPlugin {


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init config plugin");
        Config config = new Config();
        if (ioc.containsBean(Config.class.getName())) {
            config = ioc.getBean(Config.class);
        } else {
            ioc.putBean(config);
        }
        config.forEach((k, v) -> ioc.putBean("$" + k, v));
    }

    /**
     * 把属性注入进去
     *
     * @param ioc
     * @param annotation
     * @return
     */
    @Override
    public Optional<String> ioc(Ioc ioc, Annotation[] annotation) {
        Optional<Annotation> optional = getAnno(annotation, Value.class);
        if (optional.isPresent()) {
            Value value = (Value) optional.get();
            if (!ioc.containsBean(value.value())) {
                ioc.putBean(value.value(), value.defaultValue());
            }
            return Optional.of(value.value());
        }
        return Optional.empty();
    }
}
