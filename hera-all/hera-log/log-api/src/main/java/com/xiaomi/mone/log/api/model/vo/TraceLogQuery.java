package com.xiaomi.mone.log.api.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TraceLogQuery implements Serializable {
    private Long appId;
    private String ip;
    private String traceId;
    private String generationTime;
    private String level;
    // 最大返回数据条数
    private Integer total = 1000;
    // es查询过期时间（毫秒）
    private Long timeout = 2000L;

    public TraceLogQuery(Long appId, String ip, String traceId) {
        this.appId = appId;
        this.ip = ip;
        this.traceId = traceId;
    }

    public TraceLogQuery() {
    }
}
