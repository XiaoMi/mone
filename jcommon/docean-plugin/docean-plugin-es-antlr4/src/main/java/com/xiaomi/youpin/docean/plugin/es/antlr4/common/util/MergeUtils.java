package com.xiaomi.youpin.docean.plugin.es.antlr4.common.util;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/3 17:25
 */
public class MergeUtils {
    public static SearchSourceBuilder MergeAnd(SearchSourceBuilder builder1, SearchSourceBuilder builder2) {
        SearchSourceBuilder res = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (null != builder1) {
            boolQueryBuilder.must(builder1.query());
        }
        if (null != builder2) {
            boolQueryBuilder.must(builder2.query());
        }
        res.query(boolQueryBuilder);
        return res;
    }

    public static SearchSourceBuilder MergeOr(SearchSourceBuilder builder1, SearchSourceBuilder builder2) {
        SearchSourceBuilder res = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.should(builder1.query());
        boolQueryBuilder.should(builder2.query());
        boolQueryBuilder.minimumShouldMatch(1);
        res.query(boolQueryBuilder);
        return res;
    }

    public static SearchSourceBuilder MergeNot(SearchSourceBuilder sourceBuilder) {
        SearchSourceBuilder res = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = (BoolQueryBuilder) sourceBuilder.query();
        res.query(new BoolQueryBuilder().mustNot(boolQueryBuilder));
        return res;
    }

    /**
     * 检验value值是否为 .g4文件定义得String，即以双引号开头和结尾
     *
     * @param text string
     * @return boolean
     */
    public static boolean isString(String text) {
        return (text.startsWith("/\"") && text.endsWith("\""));
    }

    public void andMergeMust(BoolQueryBuilder boolQueryBuilder, List<QueryBuilder> mustList) {
        if (!mustList.isEmpty()) {
            mustList.stream().map(boolQueryBuilder::must);
        }
        //return boolQueryBuilder;
    }

    public static void andMergeMustNot(BoolQueryBuilder boolQueryBuilder, List<QueryBuilder> mustNotList) {
        if (!mustNotList.isEmpty()) {
            mustNotList.stream().map(boolQueryBuilder::mustNot);
        }
        //return boolQueryBuilder;
    }

    public BoolQueryBuilder andMergeShould(List<QueryBuilder> shouldList1, List<QueryBuilder> shouldList2) {
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
        if (!shouldList1.isEmpty()) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            shouldList1.stream().map(boolQueryBuilder::should);
            queryBuilder.must(boolQueryBuilder);
        }
        if (!shouldList2.isEmpty()) {
            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            shouldList2.stream().map(boolQueryBuilder::should);
            queryBuilder.must(boolQueryBuilder);
        }
        return queryBuilder;
    }

    public BoolQueryBuilder andMergeFilter(BoolQueryBuilder boolQueryBuilder, List<QueryBuilder> filterList) {
        if (!filterList.isEmpty()) {
            filterList.stream().map(boolQueryBuilder::filter);
        }
        return boolQueryBuilder;
    }
}

