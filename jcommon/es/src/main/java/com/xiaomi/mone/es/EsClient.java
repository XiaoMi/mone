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

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 */
public class EsClient {


    private RestHighLevelClient client;

    public EsClient(String ip, int port) {
        this.client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(ip, port, "http")));
    }


    public void insertDoc(String index, Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString()).source(data);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexResponse);
    }

    public void createIndex(String mapping) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("test");
        request.mapping(mapping, XContentType.JSON);
        RequestOptions options = RequestOptions.DEFAULT;
        client.indices().create(request, options);
    }

    public GetResponse get(GetRequest getRequest) throws IOException{
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse;
    }




}
