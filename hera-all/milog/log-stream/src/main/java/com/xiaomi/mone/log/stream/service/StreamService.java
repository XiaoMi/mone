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

package com.xiaomi.mone.log.stream.service;

import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * @Author goodjava@qq.com
 * @Date 2021/6/22 14:01
 */
@Slf4j
@Service
public class StreamService {

    @Resource
    private DefaultMQPushConsumer consumer;


    @Resource
    private EsService esService;


    private static String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        return sdf.format(new Date());
    }

    private void insertDoc(int n) {
        for (int i = 0; i < n; i++) {
            Map<String, Object> m2 = Maps.newHashMap();
            m2.put("timestamp", Instant.now().toEpochMilli());
            m2.put("message", "bbb" + i);
            try {
                String indexName = "auto_create_index-" + getTime();
                esService.insertDoc(indexName, m2);
            } catch (Throwable e) {
                log.error("es insert error:", e);
            }
        }
    }

}
