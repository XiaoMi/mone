package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.util.Map;

@Data
public class EsStatisticParam {
    private String logStoreName;
    private Long start;
    private Long end;
    private Map<String, Object> params;
}
