package com.xiaomi.mone.log.stream.job;

import com.xiaomi.mone.log.stream.plugin.loki.LokiClient;
import com.xiaomi.mone.log.stream.plugin.loki.impl.HttpLokiClient;

import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/22 15:44
 */
public class LokiSender implements MessageSender {

    public final LokiClient lokiClient;

    private final LogConfig logConfig;

    public LokiSender(LogConfig logConfig) {
        this.logConfig = logConfig;
        lokiClient = new HttpLokiClient();
    }

    @Override
    public Boolean send(Map<String, Object> data) throws Exception {
        if (lokiClient.getConfig().enabled) {
            lokiClient.send(data, lokiClient.buildFixedTags(logConfig.getLogTailId(),
                    logConfig.getLogStoreId(), logConfig.getLogSpaceId()), logConfig.getLogSpaceId().toString());
        }
        return true;
    }
}
