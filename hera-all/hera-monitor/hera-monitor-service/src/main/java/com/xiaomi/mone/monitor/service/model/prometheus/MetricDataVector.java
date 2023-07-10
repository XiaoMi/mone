package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/8/26 8:58 下午
 */
@Data
public class MetricDataVector implements Serializable {
    private String resultType;
    private List<MetricDataSetVector> result;
}
