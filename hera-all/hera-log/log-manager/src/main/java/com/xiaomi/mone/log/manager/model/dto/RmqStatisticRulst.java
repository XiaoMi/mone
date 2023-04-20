package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RmqStatisticRulst {
    private String cluster;
    private String topic;
    private Properties properties;
    private Map<String, Double> timestamps;
}
