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
package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;

import java.util.List;
import java.util.Objects;

@Data
public class EsStatisticResult {
    private List<String> timestamps;
    private List<Long> counts;
    private String name;
    private Long totalCounts;
    private BoolQueryBuilder queryBuilder;

    public void calTotalCounts() {
        if (CollectionUtils.isEmpty(counts)) {
            this.totalCounts = 0L;
            return;
        }
        this.totalCounts = counts.parallelStream()
                .filter(Objects::nonNull)
                .reduce(Long::sum).orElse(0L);
    }
}
