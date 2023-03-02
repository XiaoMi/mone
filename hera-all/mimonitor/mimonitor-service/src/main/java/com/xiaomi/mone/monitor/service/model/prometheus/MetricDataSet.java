package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/8/16 11:53 上午
 */
@Data
public class MetricDataSet implements Serializable {
    private Metric metric;
    private List<List<Long>> values;
}
