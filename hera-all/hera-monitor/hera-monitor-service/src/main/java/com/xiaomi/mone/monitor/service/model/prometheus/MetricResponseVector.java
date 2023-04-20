package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author gaoxihui
 * @date 2021/8/26 9:00 下午
 */
@Data
public class MetricResponseVector implements Serializable {
    private String status;
    private MetricDataVector data;
}
