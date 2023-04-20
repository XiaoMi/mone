package com.xiaomi.mone.log.manager.service.statement;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

public class OrStatementMatchParse implements StatementMatchParse {
    @Override
    public BoolQueryBuilder matchBuild(List<QueryEntity> queryEntities) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryEntity entity : queryEntities) {
            boolQueryBuilder.should(QueryBuilders.matchQuery(entity.getField(), entity.getFieldValue()));
        }
        boolQueryBuilder.minimumShouldMatch(1);
        return boolQueryBuilder;
    }
}
