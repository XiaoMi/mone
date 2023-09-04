/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.bind;

import cn.hutool.core.util.ClassUtil;
import com.xiaomi.mone.log.common.Config;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.mapper.MilogLogTemplateMapper;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/12/23 14:19
 */
@Component
public class LogTypeProcessorFactory {

    private static final ConcurrentHashMap<String, LogTypeProcessor> SPI_LOG_MAP = new ConcurrentHashMap<>();
    private Config config = Config.ins();
    @Setter
    private MilogLogTemplateMapper milogLogTemplateMapper;

    public LogTypeProcessor getLogTypeProcessor() {
        if (SPI_LOG_MAP.isEmpty()) {
            Class<?> logTypeProcessor = getTypeProcessor(LogTypeProcessor.class);
            LogTypeProcessor processor = createLogTypeProcessor(logTypeProcessor, config, milogLogTemplateMapper);
            SPI_LOG_MAP.putIfAbsent(logTypeProcessor.getClass().getName(), processor);
        }
        if (SPI_LOG_MAP.isEmpty()) {
            throw new MilogManageException("LogTypeProcessor not exist");
        }
        return SPI_LOG_MAP.values().stream().findAny().get();
    }

    private LogTypeProcessor createLogTypeProcessor(Class<?> logTypeProcessor, Config config, MilogLogTemplateMapper milogLogTemplateMapper) {
        if (Objects.equals(logTypeProcessor.getName(), ConfigLogTypeProcessor.class.getName())) {
            return new ConfigLogTypeProcessor(config);
        }
        if (Objects.equals(logTypeProcessor.getName(), DataSourceLogTypeProcessor.class.getName())) {
            if (null == milogLogTemplateMapper) {
                throw new MilogManageException("logTemplateMapper is null");
            }
            return new DataSourceLogTypeProcessor(milogLogTemplateMapper);
        }
        throw new MilogManageException("Unknown LogTypeProcessor class: " + logTypeProcessor.getName());
    }

    @Nullable
    private static Class<?> getTypeProcessor(Class<LogTypeProcessor> typeProcessorClass) {
        List<Class<?>> classList = getClasses(typeProcessorClass).stream()
                .filter(processorClass -> {
                    Processor processor = processorClass.getAnnotation(Processor.class);
                    return null != processor && processor.isDefault();
                })
                .sorted(Comparator.comparingInt(o -> o.getAnnotation(Processor.class).order()))
                .collect(Collectors.toList());
        return classList.stream().findFirst().get();
    }

    @NotNull
    private static List<Class<?>> getClasses(Class<LogTypeProcessor> LogTypeProcessorClass) {
        Set<Class<?>> classSet = ClassUtil.scanPackage("com.xiaomi.mone.log.manager");
        List<Class<?>> classes = classSet.parallelStream()
                .filter(LogTypeProcessorClass::isAssignableFrom)
                .filter(clazz -> !Objects.equals(clazz, LogTypeProcessorClass))
                .collect(Collectors.toList());
        return classes;
    }
}
