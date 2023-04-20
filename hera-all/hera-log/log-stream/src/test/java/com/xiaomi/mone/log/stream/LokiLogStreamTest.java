package com.xiaomi.mone.log.stream;

import com.google.gson.Gson;
import com.xiaomi.mone.log.stream.plugin.loki.LokiConfig;
import com.xiaomi.mone.log.stream.plugin.loki.LokiRequestBatcher;
import com.xiaomi.mone.log.stream.plugin.loki.LokiLogStream;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class LokiLogStreamTest {
    @Test
    public void testLokiNormalStreamParse() {
        String expectJson = "{\"streams\":[{\"stream\":{\"foo\":\"bar2\"},\"values\":[[\"1570818238000000000\",\"fizzbuzz\"]]}]}";
        String tenantId = "hera_test";

        LokiRequestBatcher streams = new LokiRequestBatcher(new LokiConfig());
        streams.add(new ArrayList<>(Arrays.asList(new LokiLogStream(new HashMap<String, Object>() {{
            put("foo", "bar2");
        }}, new ArrayList<List<String>>() {{
            add(Arrays.asList("1570818238000000000", "fizzbuzz"));
        }}))), tenantId);

        String actualJson = new Gson().
                newBuilder().excludeFieldsWithoutExposeAnnotation().
                create().toJson(streams.getStreamsMap().get(tenantId));

        assertEquals(expectJson, actualJson);
    }
}