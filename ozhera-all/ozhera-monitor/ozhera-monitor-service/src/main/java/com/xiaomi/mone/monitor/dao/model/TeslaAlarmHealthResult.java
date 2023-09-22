package com.xiaomi.mone.monitor.dao.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author gaoxihui
 */
@Data
@ToString
public class TeslaAlarmHealthResult {

    private String groupName;
    private String baseUrl;
    private Integer plateFormType;
    private Integer alarmNum;
}
