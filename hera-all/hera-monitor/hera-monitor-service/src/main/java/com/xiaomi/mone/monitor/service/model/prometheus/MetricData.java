package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/8/16 11:51 上午
 */
@Data
public class MetricData implements Serializable {
    private String resultType;
    private List<MetricDataSet> result;
}
