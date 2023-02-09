package com.xiaomi.youpin.docean.plugin.es;

import com.xiaomi.mone.es.EsClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.action.bulk.BulkProcessor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EsProcessorConf {
    private int bulkActions;

    private long byteSize;

    private int concurrentRequest;

    private int flushInterval;

    private  int retryNumber;

    private int retryInterval;

    private BulkProcessor.Listener listener;
}
