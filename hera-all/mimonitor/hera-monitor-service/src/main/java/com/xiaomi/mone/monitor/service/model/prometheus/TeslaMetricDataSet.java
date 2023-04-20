package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangxiaowei
 */
@Data
public class TeslaMetricDataSet  implements Serializable {
    private TeslaMetric metric;
    private List<List<Long>> values;
}
