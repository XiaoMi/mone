package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class StatisticRulst {
    private String name;
    private Map<String, Double> timestamps;
}
