package com.xiaomi.mone.log.stream.plugin.es;

import lombok.Data;

@Data
public class EsConfig {
    private int bulkActions;

    private long byteSize;

    private int concurrentRequest;

    private int flushInterval;

    private int retryNumber;

    private int retryInterval;
}
