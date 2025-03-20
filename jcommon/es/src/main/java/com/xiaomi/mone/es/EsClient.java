package com.xiaomi.mone.es;

import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
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
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
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
    private static Sniffer sniffer;
    private static final int SNIFF_INTERVAL_MILLIS = 60 * 1000 * 3;
    private static final int SNIFF_AFTER_FAILURE_DELAY_MILLIS = 60 * 1000;
    private static final int MAX_CONN_PER_ROUTE = 500;
    private static final int MAX_CONN_TOTAL = 500;
    private static final int SOCKET_TIMEOUT_MS = 10 * 60 * 1000;
    private static final int CONNECTION_REQUEST_TIMEOUT_MS = 5000 * 1000;
    private static final int CONNECT_TIMEOUT_MS = 5000 * 1000;
    private static final long KEEP_ALIVE_DURATION_MS = TimeUnit.MINUTES.toMillis(2);

    private RestHighLevelClient client;

    private RestClient restClient;

    public static boolean startedSniffer = true;

    private SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();

    public EsClient(String esAddr, String token, String catalog, String database) {
        validateParams(esAddr, token, catalog, database);

        Header[] defaultHeaders = createDefaultHeaders(token, catalog, database);

        RestClientBuilder builder = createRestClientBuilder(esAddr, defaultHeaders);

        initializeHighLevelClient(builder);

        initializeSnifferIfNeeded();
    }

    private void validateParams(String esAddr, String token, String catalog, String database) {
        if (esAddr == null || esAddr.isEmpty() || token == null || token.isEmpty() || catalog == null || catalog.isEmpty() || database == null || database.isEmpty()) {
            throw new IllegalArgumentException("Invalid parameters provided");
        }
    }

    private Header[] createDefaultHeaders(String token, String catalog, String database) {
        return new Header[]{
                new BasicHeader("Authorization", token),
                new BasicHeader("catalog", catalog),
                new BasicHeader("database", database)
        };
    }

    private void initializeSnifferIfNeeded() {
        if (startedSniffer) {
            initializeSniffer();
        }
    }

    private RestClientBuilder createRestClientBuilder(String esAddr, Header[] defaultHeaders) {
        String[] esAddrParts = esAddr.split(":");
        if (esAddrParts.length != 2) {
            throw new IllegalArgumentException("Invalid Elasticsearch address");
        }

        String host = esAddrParts[0];
        int port = Integer.parseInt(esAddrParts[1]);

        return RestClient.builder(new HttpHost(host, port, "http"))
                .setDefaultHeaders(defaultHeaders)
                .setFailureListener(startedSniffer ? sniffOnFailureListener : new RestClient.FailureListener())
                .setHttpClientConfigCallback(x -> x.setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                        .setMaxConnTotal(MAX_CONN_TOTAL)
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS)
                                .setConnectTimeout(CONNECT_TIMEOUT_MS)
                                .build())
                        .setKeepAliveStrategy((response, context) -> KEEP_ALIVE_DURATION_MS)
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build()));
    }


    public EsClient(String esAddr, String user, String pwd) {
//        validateParams(esAddr, user, pwd);

        List<HttpHost> hosts = createHttpHosts(esAddr);

        Header[] headers = createHeaders(user, pwd);

        RestClientBuilder clientBuilder = createRestClientBuilder(hosts, headers);

        initializeHighLevelClient(clientBuilder);

        initializeSnifferIfNeeded();
    }

    private void validateParams(String esAddr, String user, String pwd) {
        if (esAddr == null || esAddr.isEmpty() || user == null || user.isEmpty() || pwd == null || pwd.isEmpty()) {
            throw new IllegalArgumentException("Invalid parameters provided");
        }
    }

    private List<HttpHost> createHttpHosts(String esAddr) {
        String[] addrs = esAddr.split(",");
        List<HttpHost> hosts = new ArrayList<>();
        for (String addr : addrs) {
            String[] hostAndPort = addr.split(":");
            String host = hostAndPort[0];
            int port = Integer.parseInt(hostAndPort[1]);
            hosts.add(new HttpHost(host, port));
        }
        return hosts;
    }

    private Header[] createHeaders(String user, String pwd) {
        String urlEncodePassword = new String(Base64.getUrlEncoder().encode(String.format("%s:%s", user, pwd).getBytes()));
        String basicAuth = String.format("Basic %s", urlEncodePassword);
        return new Header[]{new BasicHeader("Authorization", basicAuth), new BasicHeader("Content-Type", "application/json")};
    }

    private RestClientBuilder createRestClientBuilder(List<HttpHost> hosts, Header[] headers) {
        return RestClient.builder(hosts.toArray(new HttpHost[0]))
                .setDefaultHeaders(headers)
                .setFailureListener(startedSniffer ? sniffOnFailureListener : new RestClient.FailureListener())
                .setHttpClientConfigCallback(x -> x.setMaxConnPerRoute(MAX_CONN_PER_ROUTE)
                        .setMaxConnTotal(MAX_CONN_TOTAL)
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setSocketTimeout(SOCKET_TIMEOUT_MS)
                                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT_MS)
                                .setConnectTimeout(CONNECT_TIMEOUT_MS)
                                .build())
                        .setKeepAliveStrategy((response, context) -> KEEP_ALIVE_DURATION_MS)
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setSoKeepAlive(true).build()));
    }

    private void initializeHighLevelClient(RestClientBuilder clientBuilder) {
        this.client = new RestHighLevelClient(clientBuilder);

        this.restClient = client.getLowLevelClient();
    }

    private void initializeSniffer() {
        sniffer = Sniffer.builder(restClient)
                .setSniffIntervalMillis(SNIFF_INTERVAL_MILLIS)
                .setSniffAfterFailureDelayMillis(SNIFF_AFTER_FAILURE_DELAY_MILLIS)
                .setNodesSniffer(new ElasticsearchNodesSniffer(
                        restClient,
                        TimeUnit.SECONDS.toMillis(60),
                        ElasticsearchNodesSniffer.Scheme.HTTP))
                .build();
        sniffOnFailureListener.setSniffer(sniffer);
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

    public void insertDoc(String index, Map<String, Object> data, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id).source(data);
        indexRequest.opType(DocWriteRequest.OpType.CREATE);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDocForIndex(String index, Map<String, Object> data) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", UUID.randomUUID().toString()).source(data);
        indexRequest.opType(DocWriteRequest.OpType.INDEX);
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
    }

    public void insertDocForIndex(String index, Map<String, Object> data, String id) throws IOException {
        IndexRequest indexRequest = new IndexRequest(index, "_doc", id).source(data);
        indexRequest.opType(DocWriteRequest.OpType.INDEX);
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

    public EsRet dateHistogram(String indexName, String interval, long startTime, long endTime, BoolQueryBuilder builder) throws IOException {
        return dateHistogram(indexName, "timestamp", interval, startTime, endTime, builder);
    }

    /**
     * 数据直方图
     *
     * @return
     */
    public EsRet dateHistogram(String indexName, String field, String interval, long startTime, long endTime, BoolQueryBuilder builder) throws IOException {
        // 聚合
        EsRet esRet = new EsRet();
        AggregationBuilder aggregationBuilder = AggregationBuilders.dateHistogram("dateHistogram")
                .minDocCount(0)//返回空桶
                .fixedInterval(new DateHistogramInterval(interval)) //设置间隔
                .field(field)
                .timeZone(TimeZone.getTimeZone("GMT+8").toZoneId())
                .format("yyyy-MM-dd HH:mm:ss")//设定返回格式
                .extendedBounds(new LongBounds(startTime, endTime));//统计范围
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(builder).aggregation(aggregationBuilder).size(0).timeout(TimeValue.timeValueSeconds(10L));
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
