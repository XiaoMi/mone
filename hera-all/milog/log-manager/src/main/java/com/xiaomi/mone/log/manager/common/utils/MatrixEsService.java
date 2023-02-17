package com.xiaomi.mone.log.manager.common.utils;

import com.xiaomi.mone.es.EsClient;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.io.IOException;

public class MatrixEsService extends EsService{
    private MatrixEsClient esClient;

    public MatrixEsService(String address, String useranme, String password, String catalog, String database, String workspaceToken) {
        super(address, useranme, password);
        this.esClient = new MatrixEsClient(address, useranme, password, catalog, database, workspaceToken);
    }

    @Override
    public EsClient.EsRet dateHistogram(String indexName, String interval, long startTime, long endTime, BoolQueryBuilder builder) throws IOException {
        return this.esClient.dateHistogram(indexName, interval, startTime, endTime, builder);
    }

    @Override
    public Long count(CountRequest countRequest) throws IOException {
        return this.esClient.count(countRequest);
    }

    @Override
    public SearchResponse search(SearchRequest searchRequest) throws IOException {
        return this.esClient.search(searchRequest);
    }
}
