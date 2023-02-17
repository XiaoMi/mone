package com.xiaomi.mone.log.manager.common.utils;

import com.xiaomi.mone.es.EsClient;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.histogram.LongBounds;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MatrixEsClient extends EsClient {
    private RestHighLevelClient matrixClient;
    public MatrixEsClient(String esAddr, String user, String pwd, String catalog, String database, String workspaceToken) {
        super(esAddr, user, pwd);
        String[] addrs = esAddr.split(",");
        List<HttpHost> hosts = new ArrayList();
        String[] var6 = addrs;
        int var7 = addrs.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            String addr = var6[var8];
            String[] hostAndPort = addr.split(":");
            int port = Integer.parseInt(hostAndPort[1]);
            HttpHost host = new HttpHost(hostAndPort[0], port);
            hosts.add(host);
        }

        Header[] headers = new Header[]{
                new BasicHeader("Authorization", workspaceToken),
                new BasicHeader("catalog", catalog),
                new BasicHeader("database", database)
        };
        RestClientBuilder clientBuilder = RestClient.builder((HttpHost[])hosts.toArray(new HttpHost[0])).setDefaultHeaders(headers).setHttpClientConfigCallback((x) -> {
            return x.setMaxConnPerRoute(500).setMaxConnTotal(500).setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(120000).setConnectTimeout(5000000).build()).setKeepAliveStrategy((response, context) -> {
                return TimeUnit.MINUTES.toMillis(2L);
            });
        });
        this.matrixClient = new RestHighLevelClient(clientBuilder);
    }

    @Override
    public EsClient.EsRet dateHistogram(String indexName, String interval, long startTime, long endTime, BoolQueryBuilder builder) throws IOException {
        EsClient.EsRet esRet = new EsClient.EsRet();
        AggregationBuilder aggregationBuilder = ((DateHistogramAggregationBuilder)((DateHistogramAggregationBuilder)((DateHistogramAggregationBuilder) AggregationBuilders.dateHistogram("dateHistogram").minDocCount(0L).fixedInterval(new DateHistogramInterval(interval)).field("alpha_timestamp")).timeZone(TimeZone.getTimeZone("GMT+8").toZoneId())).format("yyyy-MM-dd HH:mm:ss")).extendedBounds(new LongBounds(startTime, endTime));
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(builder).aggregation(aggregationBuilder).size(0);
        searchRequest.source(searchSourceBuilder);
        searchRequest.indices(new String[]{indexName});
        SearchResponse searchResponse = this.matrixClient.search(searchRequest, RequestOptions.DEFAULT);
        if (searchResponse != null && searchResponse.getAggregations() != null) {
            Aggregation agg = searchResponse.getAggregations().get("dateHistogram");
            List<? extends Histogram.Bucket> buckets = ((Histogram)agg).getBuckets();
            List<String> timestamps = new ArrayList();
            List<Long> counts = new ArrayList();
            Iterator var17 = buckets.iterator();

            while(var17.hasNext()) {
                Histogram.Bucket bucket = (Histogram.Bucket)var17.next();
                timestamps.add(bucket.getKeyAsString());
                counts.add(bucket.getDocCount());
            }

            esRet.setCounts(counts);
            esRet.setTimestamps(timestamps);
            return esRet;
        } else {
            return esRet;
        }
    }

    @Override
    public Long count(CountRequest countRequest) throws IOException {
        CountResponse countResponse = this.matrixClient.count(countRequest, RequestOptions.DEFAULT);
        long count = countResponse.getCount();
        return count;
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        SearchResponse res = this.matrixClient.search(searchRequest, RequestOptions.DEFAULT);
        return res;
    }
}
