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
package com.xiaomi.mone.log.agent;

import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.comparator.OutputSimilarComparator;
import com.xiaomi.mone.log.agent.output.RmqOutput;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/29 19:52
 */
@Slf4j
public class Comparator {

    private Gson gson = new Gson();

    @Test
    public void testCompare() {
        String msg1 = "{\"clusterInfo\":\"http://127.0.0.1\",\"producerGroup\":\"subGroup_tags_1_4_620\",\"ak\":\"\",\"sk\":\"\",\"topic\":\"mone_hera_staging_trace_etl_server\",\"type\":\"talos\",\"tag\":\"tags_1_4_620\"}";
        RmqOutput outputOld = gson.fromJson(msg1, RmqOutput.class);
        OutputSimilarComparator outputSimilarComparator = new OutputSimilarComparator(outputOld);
        String msg2 = "{\"clusterInfo\":\"http://127.0.0.1\",\"producerGroup\":\"subGroup_tags_1_4_620\",\"ak\":\"\",\"sk\":\"\",\"topic\":\"mione_staging_jaeger_etl_sidecar_first\",\"type\":\"talos\",\"tag\":\"tags_1_4_620\"}";
        RmqOutput outputNew = gson.fromJson(msg2, RmqOutput.class);
        log.info("result:{}", outputSimilarComparator.compare(outputNew));
    }
}
