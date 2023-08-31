package com.xiaomi.mone.monitor.pojo;


import lombok.Data;

import java.util.List;

/**
 * @author gaoxihui
 */
@Data
public class ReqErrorMetricsPOJO {
    private String code;
    private String message;
    private List<AlarmPresetMetricsPOJO> metrics;

}
