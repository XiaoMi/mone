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

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.io.IOException;
import java.util.*;

/**
 * @author goodjava@qq.com
 */
public class EsClient {


    private RestHighLevelClient client;

    public EsClient(String esAddr, String user, String pwd) {

        String[] addrs = esAddr.split(",");
        List<HttpHost> hosts = new ArrayList<>();
        for (String addr : addrs) {
            String[] hostAndPort = addr.split(":");
            int port = Integer.parseInt(hostAndPort[1]);
            HttpHost host = new HttpHost(hostAndPort[0], port);
            hosts.add(host);
        }

        String urlencodePassword = new String(Base64.getUrlEncoder().encode(String.format("%s:%s", user, pwd).getBytes()));
        String basicAuth = String.format("basic %s", urlencodePassword);
        Header[] headers = new Header[] {new BasicHeader("Authorization", basicAuth)};

        RestClientBuilder clientBuilder = RestClient.builder(hosts.toArray(new HttpHost[0])).setDefaultHeaders(headers);

        this.client = new RestHighLevelClient(clientBuilder);
    }

    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        SearchResponse res = this.client.search(searchRequest, RequestOptions.DEFAULT);
        return res;
    }


    public void insertDoc(String index, Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString()).source(data);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDocJson(String index, String jsonString) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString()).source(jsonString, XContentType.JSON);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void createIndex(String name,String mapping) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(name);
        request.mapping(mapping, XContentType.JSON);
        RequestOptions options = RequestOptions.DEFAULT;
        client.indices().create(request, options);
    }

    public GetResponse get(GetRequest getRequest) throws IOException{
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse;
    }

    public DeleteResponse delete(DeleteRequest request) throws IOException {
        DeleteResponse resp = client.delete(request, RequestOptions.DEFAULT);
        return resp;
    }

    public UpdateResponse update(UpdateRequest request) throws IOException {
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response;
    }




}
