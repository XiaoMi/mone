package com.xiaomi.mone.log.manager.service.statement;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.List;

import static com.xiaomi.mone.log.manager.service.statement.StatementMatchParseFactory.DOUBLE_QUOTATION_MARK_SEPARATOR;

/**
 * must语句匹配
 */
public class MustStatementMatchParse implements StatementMatchParse {
    @Override
    public BoolQueryBuilder matchBuild(List<QueryEntity> queryEntities) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        for (QueryEntity entity : queryEntities) {
            if (entity.getFieldValue().startsWith(DOUBLE_QUOTATION_MARK_SEPARATOR)) {
                boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(entity.getField(), entity.getFieldValue()));
            } else {
                boolQueryBuilder.must(QueryBuilders.matchQuery(entity.getField(), entity.getFieldValue()));
            }
        }
        return boolQueryBuilder;
    }
}
