package com.xiaomi.mone.es;

import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.NodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
public class EsClient {

    private static RestClientBuilder restClientBuilder;
    private static Sniffer sniffer;
    private static final int TIME_OUT = 10 * 60 * 1000;
    private static final int SNIFF_INTERVAL_MILLIS = 30 * 1000;
    private static final int SNIFF_AFTER_FAILURE_DELAY_MILLIS = 30 * 1000;

    private RestHighLevelClient client;

    private RestClient restClient;

//    public EsClient(String esAddr, String user, String pwd) {
//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, pwd));
//        RestClientBuilder builder = RestClient.builder(new HttpHost(esAddr.split(":")[0], Integer.valueOf(esAddr.split(":")[1]), "http"))
//                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
//                    @Override
//                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
//                        return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
//                    }
//                }).setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
//                    // 该方法接收一个RequestConfig.Builder对象，对该对象进行修改后然后返回。
//                    @Override
//                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder builder) {
//                        return builder.setConnectTimeout(5000 * 1000) // 连接超时（默认为1秒）
//                                .setSocketTimeout(6000 * 1000);// 套接字超时（默认为30秒）//更改客户端的超时限制默认30秒现在改为100*1000分钟
//                    }
//                });// 调整最大重试超时时间（默认为30秒）.setMaxRetryTimeoutMillis(60000)
//        this.client = new RestHighLevelClient(builder);
//
//    }

    public EsClient(String esAddr, String token, String catalog, String database) {
        Header[] defaultHeaders = new Header[]{
                new BasicHeader("Authorization", token),
                new BasicHeader("catalog", catalog),
                new BasicHeader("database", database)
        };

        RestClientBuilder builder = RestClient.builder(new HttpHost(esAddr.split(":")[0], Integer.parseInt(esAddr.split(":")[1]), "http"))
                .setDefaultHeaders(defaultHeaders)
                .setHttpClientConfigCallback(x -> x.setMaxConnPerRoute(500)
                        .setMaxConnTotal(500)
                        .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(10 * 60 * 1000)
                                .setConnectionRequestTimeout(5000 * 1000)
                                .setConnectTimeout(5000 * 1000)
                                .build())
                        .setKeepAliveStrategy((response, context) -> TimeUnit.MINUTES.toMillis(2))
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build()));
        this.client = new RestHighLevelClient(builder);
    }

    public EsClient(String esAddr, String user, String pwd) {

        String[] addrs = esAddr.split(",");
        List<HttpHost> hosts = new ArrayList<>();
        for (String addr : addrs) {
            String[] hostAndPort = addr.split(":");
            int port = Integer.parseInt(hostAndPort[1]);
            HttpHost host = new HttpHost(hostAndPort[0], port);
            hosts.add(host);
        }

        String urlEncodePassword = new String(Base64.getUrlEncoder().encode(String.format("%s:%s", user, pwd).getBytes()));
        String basicAuth = String.format("Basic %s", urlEncodePassword);
        Header[] headers = new Header[]{new BasicHeader("Authorization", basicAuth), new BasicHeader("Content-Type", "application/json")};

        RestClientBuilder clientBuilder = RestClient.builder(hosts.toArray(new HttpHost[0]))
                .setDefaultHeaders(headers)
                .setHttpClientConfigCallback(x -> x.setMaxConnPerRoute(500)
                        .setMaxConnTotal(500)
                        .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(10 * 60 * 1000)
                                .setConnectionRequestTimeout(5000 * 1000)
                                .setConnectTimeout(5000 * 1000).build())
                        .setKeepAliveStrategy((response, context) -> TimeUnit.MINUTES.toMillis(2))
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build()));
        this.client = new RestHighLevelClient(clientBuilder);

    }

    public EsClient(List<String> restAddress, int httpPort, String username, String password, int timeOut, int snifferIntervalMillis, int snifferAfterFailDelayMillis) throws IOException {
        snifferNodeInit(restAddress, httpPort, username, password, timeOut, snifferIntervalMillis, snifferAfterFailDelayMillis);
    }

    public EsClient(List<String> restAddress, int httpPort, String username, String password) throws IOException {
        snifferNodeInit(restAddress, httpPort, username, password, TIME_OUT, SNIFF_INTERVAL_MILLIS, SNIFF_AFTER_FAILURE_DELAY_MILLIS);
    }

    private void snifferNodeInit(List<String> restAddress, int httpPort, String username, String password, int timeOut, int snifferIntervalMillis, int snifferAfterFailDelayMillis) throws IOException {


        HttpHost[] hosts = new HttpHost[restAddress.size()];
        for (int index = 0; index < restAddress.size(); index++) {
            hosts[index] = new HttpHost(restAddress.get(index), httpPort, "http");
        }

        RestClientBuilder.RequestConfigCallback requestConfigCallback = new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(
                    RequestConfig.Builder requestConfigBuilder) {
                return requestConfigBuilder
                        .setConnectTimeout(timeOut)
                        .setSocketTimeout(timeOut);
            }
        };

        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(
                    HttpAsyncClientBuilder httpClientBuilder) {
                RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                        .setConnectTimeout(timeOut)
                        .setSocketTimeout(timeOut)
                        .setConnectionRequestTimeout(timeOut);
                httpClientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
                return httpClientBuilder;
            }
        };

        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        if (username != null && password != null) {
            String token = "Basic " + new String(Base64.getUrlEncoder().encode((username + ":" + password).getBytes()));
            Header[] tokenHeader = new Header[]{new BasicHeader("Authorization", token)};
            restClientBuilder = RestClient.builder(hosts).setNodeSelector(SKIP_DEDICATED_NODES)
                    .setFailureListener(sniffOnFailureListener)
                    .setHttpClientConfigCallback(httpClientConfigCallback)
                    .setRequestConfigCallback(requestConfigCallback)
                    .setDefaultHeaders(tokenHeader);
        } else {
            restClientBuilder = RestClient.builder(hosts).setNodeSelector(SKIP_DEDICATED_NODES)
                    .setFailureListener(sniffOnFailureListener)
                    .setRequestConfigCallback(requestConfigCallback)
                    .setHttpClientConfigCallback(httpClientConfigCallback);
        }

        client = new RestHighLevelClient(restClientBuilder);
        restClient = client.getLowLevelClient();

        NodesSniffer elasticsearchNodesSniffer = new ElasticsearchNodesSniffer(
                restClient,
                TimeUnit.SECONDS.toMillis(5),
                ElasticsearchNodesSniffer.Scheme.HTTP);

        // important
        sniffer = Sniffer.builder(restClient)
                .setSniffIntervalMillis(snifferIntervalMillis)
                .setSniffAfterFailureDelayMillis(snifferAfterFailDelayMillis)
                .setNodesSniffer(elasticsearchNodesSniffer)
                .build();
        sniffOnFailureListener.setSniffer(sniffer);
    }

    // important
    private NodeSelector SKIP_DEDICATED_NODES = new NodeSelector() {
        @Override
        public void select(Iterable<Node> nodes) {
            for (Iterator<Node> itr = nodes.iterator(); itr.hasNext(); ) {
                Node node = itr.next();
                if (node.getRoles() == null) continue;
                if ((node.getRoles().isMasterEligible()
                        && false == node.getRoles().isData()
                        && false == node.getRoles().isIngest())
                        ||
                        (node.getAttributes().containsKey("node_type")
                                && node.getAttributes().get("node_type").contains("client")
                                && false == node.getRoles().isData())) {
                    itr.remove();
                }
            }
        }

        @Override
        public String toString() {
            return "SKIP_DEDICATED_NODES";
        }
    };

    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        SearchResponse res = this.client.search(searchRequest, RequestOptions.DEFAULT);
        return res;
    }


    public void insertDoc(String index, Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString()).source(data);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDoc(String index, Map<String, Object> data, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id).source(data);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDocJson(String index, String jsonString) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString()).source(jsonString, XContentType.JSON);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDocJson(String index, String jsonString, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id).source(jsonString, XContentType.JSON);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDocAsyn(String index, Map<String, Object> data, ActionListener<IndexResponse> listener) {
        IndexRequest request = new IndexRequest(index).source(data);
        client.indexAsync(request, RequestOptions.DEFAULT, listener);
    }

    public void createIndex(String name, String mapping) throws IOException {
        CreateIndexRequest request = new CreateIndexRequest(name);
        request.mapping(mapping, XContentType.JSON);
        RequestOptions options = RequestOptions.DEFAULT;
        client.indices().create(request, options);
    }

    public CreateIndexResponse createIndex(CreateIndexRequest request) throws IOException {
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    public org.elasticsearch.client.indices.CreateIndexResponse createIndex(org.elasticsearch.client.indices.CreateIndexRequest request) throws IOException {
        return client.indices().create(request, RequestOptions.DEFAULT);
    }

    public GetResponse get(GetRequest getRequest) throws IOException {
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        return getResponse;
    }

    public SearchResponse queryByIndex(String index) throws IOException {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("_index", index);
        sourceBuilder.query(queryBuilder);
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        return searchResponse;
    }

    public DeleteResponse delete(DeleteRequest request) throws IOException {
        DeleteResponse resp = client.delete(request, RequestOptions.DEFAULT);
        return resp;
    }

    public UpdateResponse update(UpdateRequest request) throws IOException {
        UpdateResponse response = client.update(request, RequestOptions.DEFAULT);
        return response;
    }

    /**
     * 批量保存 可能会丢失数据，建议使用bulkProcessor
     *
     * @param index
     * @param dataList
     * @return
     * @throws IOException
     */
    @Deprecated
    public boolean bulkInsert(String index, List<Map<String, Object>> dataList) throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        for (int i = 0; i < dataList.size(); i++) {
            bulkRequest.add(new IndexRequest(index).source(dataList.get(i)));
        }
        bulkRequest.timeout(TimeValue.timeValueSeconds(5));
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    /**
     * 创建索引模板
     *
     * @param request
     * @return
     * @throws IOException
     */
    public boolean createIndexTemplate(PutIndexTemplateRequest request) throws IOException {
        AcknowledgedResponse response = client.indices().putTemplate(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 索引模板是否存在
     *
     * @param request
     * @return
     * @throws IOException
     */
    public boolean existsTemplate(IndexTemplatesExistRequest request) throws IOException {
        return client.indices().existsTemplate(request, RequestOptions.DEFAULT);
    }

    /**
     * 获取索引模板
     *
     * @param request
     * @return
     * @throws IOException
     */
    public List<IndexTemplateMetadata> getIndexTemplates(GetIndexTemplatesRequest request) throws IOException {
        GetIndexTemplatesResponse templatesResponse = client.indices().getIndexTemplate(request, RequestOptions.DEFAULT);
        return templatesResponse.getIndexTemplates();
    }

    /**
     * 统计
     *
     * @param countRequest
     * @return
     * @throws IOException
     */
    public Long count(CountRequest countRequest) throws IOException {
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        long count = countResponse.getCount();
        return count;
    }

    /**
     * 数据直方图
     *
     * @return
     */
    public EsRet dateHistogram(String indexName, String interval, long startTime, long endTime, BoolQueryBuilder builder) throws IOException {
        // 聚合
        EsRet esRet = new EsRet();
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("dateHistogram")
                .minDocCount(0)//返回空桶
                .fixedInterval(new DateHistogramInterval(interval)) //设置间隔
                .field("timestamp")
                .timeZone(TimeZone.getTimeZone("GMT+8").toZoneId())
                .format("yyyy-MM-dd HH:mm:ss")//设定返回格式
                .extendedBounds(new LongBounds(startTime, endTime));//统计范围
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(builder).aggregation(aggregationBuilder).size(0).timeout(TimeValue.timeValueSeconds(10l));
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(indexName);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse == null || searchResponse.getAggregations() == null) {
            return esRet;
        }
        Aggregation agg = searchResponse.getAggregations().get("dateHistogram");
        List<? extends Histogram.Bucket> buckets = ((Histogram) agg).getBuckets();
        List<String> timestamps = new ArrayList<>();
        List<Long> counts = new ArrayList<>();
        for (Histogram.Bucket bucket : buckets) {
            timestamps.add(bucket.getKeyAsString());
            counts.add(bucket.getDocCount());
        }
        esRet.setCounts(counts);
        esRet.setTimestamps(timestamps);
        return esRet;
    }

    public Integer getClusterHealth() throws IOException {
        ClusterHealthRequest request = new ClusterHealthRequest();
        request.timeout(TimeValue.timeValueSeconds(50));
        ClusterHealthResponse response = client.cluster().health(request, RequestOptions.DEFAULT);
        return response.status().getStatus();
    }

    public void searchAsync(SearchRequest searchRequest, ActionListener<SearchResponse> listener) {
        client.searchAsync(searchRequest, RequestOptions.DEFAULT, listener);
    }

    @Data
    public class EsRet {
        private List<String> timestamps;
        private List<Long> counts;
    }

    public RestHighLevelClient getEsOriginalClient() {
        return this.client;
    }

    public void close() throws IOException {
        this.client.close();
        if (sniffer != null) {
            sniffer.close();
        }
    }

    /**
     * query index mapping
     *
     * @param indexName
     * @return
     * @throws IOException
     */
    public GetMappingsResponse queryIndexMapping(String indexName) throws IOException {
        GetMappingsRequest request = new GetMappingsRequest().indices(indexName);
        return client.indices().getMapping(request, RequestOptions.DEFAULT);
    }

}
