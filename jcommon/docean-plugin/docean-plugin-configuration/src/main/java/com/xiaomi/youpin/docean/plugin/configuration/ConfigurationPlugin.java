/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.plugin.configuration;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Configuration;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.Pair;
import com.xiaomi.youpin.docean.common.ReflectUtils;
import com.xiaomi.youpin.docean.ioc.BeanAnnoProcessor;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import com.xiaomi.youpin.docean.plugin.config.ConfigUtils;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * @author shanwb
 * @date 2022-09-13
 */
@DOceanPlugin(order = Integer.MAX_VALUE - 2)
@Slf4j
public class ConfigurationPlugin implements IPlugin {

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        classSet.stream()
                .filter(this::filterConfigurationClass)
                .forEach(it -> BeanAnnoProcessor.process(makeConfigurationInstance(it, ioc), ioc));
    }

    private boolean filterConfigurationClass(Class<?> it) {
        return Optional.ofNullable(it).map(it2 -> it2.getAnnotation(Configuration.class)).isPresent();
    }

    private Object makeConfigurationInstance(Class<?> configuration, Ioc ioc) {
        Object cInstance = ReflectUtils.getInstance(configuration);
        Config config = ioc.getBean(Config.class);
        injectValue(cInstance, config);
        return cInstance;
    }

    private void injectValue(Object cInstance, Config config) {
        if (null == config) {
            log.warn("config class is null, @Configuration:{} no need to inject @Value", cInstance.getClass());
            return;
        }
        Class<?> configuration = cInstance.getClass();
        Field[] fields = ReflectUtils.fields(configuration);
        Arrays.stream(fields).forEach(f -> {
            Optional<Annotation> optional = Optional.ofNullable(f.getAnnotation(Value.class));
            if (optional.isPresent()) {
                String realValue = null;
                Value value = (Value) optional.get();
                String v = value.value();
                String dv = value.defaultValue();

                //适配 ${key:defaultValue} 格式
                if (v.startsWith("${")) {
                    Pair<String, String> elPair = ConfigUtils.parseElKey(v, dv);
                    realValue = config.get(elPair.getKey(), elPair.getValue());
                } else {
                    realValue = config.get(v, dv);
                }

                ReflectUtils.setField(cInstance, f, realValue);
            }
        });
    }

}
