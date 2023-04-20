package com.xiaomi.mone.log.manager.domain.analyse;

import com.xiaomi.mone.log.manager.model.bo.CalcuAggrParam;
import com.xiaomi.mone.log.manager.model.dto.LogAnalyseDataDTO;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;

public interface AggrCalcuStrategy {

    AggregationBuilder getAggr(CalcuAggrParam param);

    LogAnalyseDataDTO formatRes(SearchResponse response);

}
