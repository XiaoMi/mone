package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class EsStatisticResult {
    private List<String> timestamps;
    private List<Long> counts;
    private String name;
}
