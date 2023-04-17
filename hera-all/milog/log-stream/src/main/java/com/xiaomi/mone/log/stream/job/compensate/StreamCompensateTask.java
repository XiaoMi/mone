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

package com.xiaomi.mone.log.stream.job.compensate;

import com.xiaomi.mone.log.stream.common.LogStreamConstants;
import com.xiaomi.mone.log.stream.job.extension.CompensateMsgConsume;
import com.xiaomi.mone.log.stream.job.extension.CompensateMsgConsumeProvider;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * @author shanwb
 * @date 2023-04-10
 */
@Service
@Slf4j
public class StreamCompensateTask {

    @Value("${hera.stream.compensate.enable}")
    private String compensateEnable;

    @Value("${hera.stream.compensate.mq}")
    private String compensateMqType;

    public void init() {
        try {
            log.info("StreamCompensateTask go to start, enable:{}, mqType:{}", compensateEnable, compensateMqType);

            if ("true".equalsIgnoreCase(compensateEnable)) {
                String compensateMsgConsumeProviderBean = compensateMqType + LogStreamConstants.compensateMsgConsumeProviderBeanSuffix;
                CompensateMsgConsumeProvider compensateMsgConsumeProvider = Ioc.ins().getBean(compensateMsgConsumeProviderBean);
                CompensateMsgConsume mqMessageConsume = compensateMsgConsumeProvider.getCompensateMsgConsume();
                mqMessageConsume.consume();
            } else {
                log.warn("### no need to start compensate consume task");
            }
        } catch (Exception e) {
            log.error("StreamCompensateTask init exception", e);
        }
    }

}
