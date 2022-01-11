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

package com.xiaomi.mone.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;

import java.util.Map;
import java.util.concurrent.TimeUnit;
@Slf4j
public class EsProcessor {

    private RestHighLevelClient originalClient;

    private BulkProcessor bulkProcessor;

    private EsClient client;

    public EsProcessor(ProcessorConf conf) {
        this.client = conf.getEsClient();
        BulkProcessor.Builder builder = BulkProcessor.builder(
                (request, bulkListener) ->
                        originalClient.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                conf.getListener());
        builder.setBulkActions(conf.getBulkActions());
        builder.setBulkSize(new ByteSizeValue(conf.getByteSize(), ByteSizeUnit.MB));
        builder.setConcurrentRequests(conf.getConcurrentRequest());
        builder.setFlushInterval(TimeValue.timeValueMillis(conf.getFlushInterval()));
        builder.setBackoffPolicy(BackoffPolicy
                .constantBackoff(TimeValue.timeValueSeconds(conf.getRetryInterval()), conf.getRetryNumber())).build();
        this.bulkProcessor = builder.build();
        this.originalClient = conf.getEsClient().getEsOriginalClient();
    }

    public void close(){
        this.bulkProcessor.close();
    }

    public boolean awaitClose(int timeout) throws InterruptedException {
        return this.bulkProcessor.awaitClose(timeout, TimeUnit.SECONDS);
    }

    public void flush(){
        this.bulkProcessor.flush();
    }

    /**
     * 批量插入
     * @param indexName
     * @param data
     */
    public void bulkInsert(String indexName, Map<String, Object> data) {
        this.bulkProcessor.add(new IndexRequest(indexName).source(data).timeout(new TimeValue(1, TimeUnit.MINUTES)));
    }
}
