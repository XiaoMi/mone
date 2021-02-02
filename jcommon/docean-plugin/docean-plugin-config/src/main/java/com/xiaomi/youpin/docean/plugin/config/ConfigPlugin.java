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
        ioc.putBean(config);
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
