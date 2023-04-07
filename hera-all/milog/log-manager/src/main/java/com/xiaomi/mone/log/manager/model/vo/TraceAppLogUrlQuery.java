package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

@Data
public class TraceAppLogUrlQuery {
    private String traceId;
    private Long appId;
    private Long timestamp;
    private Long envId;
}
