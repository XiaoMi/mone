package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/8/16 11:25 上午
 */
@Data
public class MetricResponse implements Serializable {
    private String status;
    private MetricData data;
}
