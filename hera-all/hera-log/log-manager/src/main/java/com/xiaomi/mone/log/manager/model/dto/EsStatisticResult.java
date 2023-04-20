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
