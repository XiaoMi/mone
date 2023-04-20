package com.xiaomi.mone.monitor.service.model.prometheus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.xiaomi.mone.monitor.bo.AlarmCheckDataCount;
import com.xiaomi.mone.monitor.bo.AlarmSendInterval;
import com.xiaomi.mone.monitor.dao.model.AppAlarmRule;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2021/9/15 5:40 下午
 */
@Data
public class AppWithAlarmRules implements Serializable {

    String appName;
    String creater;
    Integer ruleStatus;//0 生效、1暂停
    Integer iamId;
    Integer projectId;
    String remark;
    Map<String, String> metricMap;
    Map<String, String> checkDataMap;
    Map<String, String> sendIntervalMap;


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    Date lastUpdateTime;
    List<AppAlarmRule> alarmRules;
}
