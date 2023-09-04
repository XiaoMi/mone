package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangxiaowei
 */
@Data
public class TeslaMetricResponse  implements Serializable {
    private String status;
    private TeslaMetricData data;
}
