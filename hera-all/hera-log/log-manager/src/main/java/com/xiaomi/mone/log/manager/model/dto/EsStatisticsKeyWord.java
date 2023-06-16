package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/6/9 11:35
 */
@Data
public class EsStatisticsKeyWord {

    private String key;

    private List<StatisticsRation> statisticsRation;
    @Data
    public static class StatisticsRation {
        private String value;
        private String ration;
    }

}
