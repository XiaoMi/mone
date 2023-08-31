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
package com.xiaomi.mone.log.stream.sink;

import com.google.common.collect.Lists;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/7 14:38
 */
@Component
@Slf4j
public class SinkChain {
    /**
     * Have you already scanned and obtained SinkProcessor
     */
    private boolean isLoad;

    private static final List<SinkProcessor> sinkProcessorList = Lists.newArrayList();

    /**
     * If one is true, then it is true. (literal translation)
     * As long as one is true, it is considered true. (more natural translation)
     *
     * @param map
     * @return
     */
    public boolean execute(Map<String, Object> map) {
        loadSinkProcessor();
        List<Boolean> res = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(sinkProcessorList)) {
            for (SinkProcessor sinkProcessor : sinkProcessorList) {
                res.add(sinkProcessor.execute(map));
            }
        }
        return res.stream().anyMatch(re -> re);
    }

    private void loadSinkProcessor() {
        if (!isLoad) {
            Set<SinkProcessor> beans = Ioc.ins().getBeans(SinkProcessor.class);
            if (CollectionUtils.isNotEmpty(beans)) {
                sinkProcessorList.addAll(beans);
            }
            isLoad = true;
        }
    }
}
