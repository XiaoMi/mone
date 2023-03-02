package com.xiaomi.mone.log.agent;

import com.google.gson.Gson;
import com.xiaomi.mone.log.agent.channel.comparator.OutputSimilarComparator;
import com.xiaomi.mone.log.agent.export.TalosOutput;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/29 19:52
 */
@Slf4j
public class comparator {

    private Gson gson = new Gson();

    @Test
    public void testCompare() {
        String msg1 = "{\"clusterInfo\":\"http://127.0.0.1\",\"producerGroup\":\"subGroup_tags_1_4_620\",\"ak\":\"\",\"sk\":\"\",\"topic\":\"mone_hera_staging_trace_etl_server\",\"type\":\"talos\",\"tag\":\"tags_1_4_620\"}";
        TalosOutput outputOld = gson.fromJson(msg1, TalosOutput.class);
        OutputSimilarComparator outputSimilarComparator = new OutputSimilarComparator(outputOld);
        String msg2 = "{\"clusterInfo\":\"http://127.0.0.1\",\"producerGroup\":\"subGroup_tags_1_4_620\",\"ak\":\"\",\"sk\":\"\",\"topic\":\"mione_staging_jaeger_etl_sidecar_first\",\"type\":\"talos\",\"tag\":\"tags_1_4_620\"}";
        TalosOutput outputNew = gson.fromJson(msg2, TalosOutput.class);
        log.info("result:{}", outputSimilarComparator.compare(outputNew));
    }
}
