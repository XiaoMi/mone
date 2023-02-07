package com.xiaomi.mone.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.action.bulk.BulkProcessor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessorConf {
    private int bulkActions;

    private long byteSize;

    private int concurrentRequest;

    private int flushInterval;

    private  int retryNumber;

    private int retryInterval;

    private EsClient esClient;

    private BulkProcessor.Listener listener;
}
