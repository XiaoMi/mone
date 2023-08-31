package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangxiaowei6
 */
@Data
public class TeslaMetricData implements Serializable {
    private String resultType;
    private List<TeslaMetricDataSet> result;
}
