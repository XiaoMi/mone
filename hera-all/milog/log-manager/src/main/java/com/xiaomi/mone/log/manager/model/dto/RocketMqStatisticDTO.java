package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class RocketMqStatisticDTO {
    private String requestId;
    private String cost;
    private List<StatisticRulst> data;
}