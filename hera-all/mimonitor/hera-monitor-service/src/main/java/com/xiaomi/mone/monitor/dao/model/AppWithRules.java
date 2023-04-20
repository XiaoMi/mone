package com.xiaomi.mone.monitor.dao.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2021/9/20 7:39 下午
 */
@Data
public class AppWithRules {
    private Integer projectId;
    private Integer iamId;
    private String projectName;
    List<AppAlarmRule> alarmRules;
}
