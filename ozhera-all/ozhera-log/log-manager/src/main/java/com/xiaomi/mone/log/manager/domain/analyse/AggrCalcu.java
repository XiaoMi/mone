/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.domain.analyse;

import com.xiaomi.mone.log.manager.model.bo.CalcuAggrParam;
import com.xiaomi.mone.log.manager.model.dto.LogAnalyseDataDTO;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.AggregationBuilder;

@Service
public class AggrCalcu {

    private AggrCalcuStrategy getStrategy(Integer graphType) {
        if (graphType == null) {
            return null;
        }
        switch (graphType) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                return Ioc.ins().getBean(FieldStrategy.class.getName());
            case 10:
                return Ioc.ins().getBean(DateGroupStrategy.class.getName());
            default:
                return null;
        }
    }

    public AggregationBuilder getAggr(CalcuAggrParam param) {
        AggrCalcuStrategy strategy = getStrategy(param.getGraphType());
        if (strategy == null) {
            return null;
        }
        return strategy.getAggr(param);
    }

    public LogAnalyseDataDTO formatRes(Integer graphType, SearchResponse response) {
        AggrCalcuStrategy strategy = getStrategy(graphType);
        if (strategy == null) {
            return null;
        }
        return strategy.formatRes(response);
    }


}
