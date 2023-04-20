package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/9/3 10:31 上午
 */
@Data
public class MiLogQuery implements Serializable {

    private Long projectId;
    private String serverIp;
    private String traceId;
    private String generationTime;
    private String level;

}
