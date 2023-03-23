package com.xiaomi.mone.log.manager.service.statement;

import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.List;

/**
 * es 语句匹配
 */
public interface StatementMatchParse {

    BoolQueryBuilder matchBuild(List<QueryEntity> queryEntities);
}
