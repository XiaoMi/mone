package com.xiaomi.mone.log.manager.domain.analyse;

import com.xiaomi.mone.log.manager.dao.LogstoreDao;
import com.xiaomi.mone.log.manager.domain.EsCluster;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogStoreDO;
import com.xiaomi.youpin.docean.plugin.es.EsService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import javax.annotation.Resource;
import java.io.IOException;

public class Top {

    @Resource
    private LogstoreDao logstoreDao;

    @Resource
    private EsCluster esCluster;

    public void caclulate(String storeName) throws IOException {
        MilogLogStoreDO store = logstoreDao.getByName(storeName);
        EsService esService = esCluster.getEsService(store.getEsClusterId());
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("store", storeName));
        AggregationBuilder top = AggregationBuilders.topHits("level").size(3);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.aggregation(top);
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.source(searchSourceBuilder);
        esService.search(searchRequest);
    }

}
