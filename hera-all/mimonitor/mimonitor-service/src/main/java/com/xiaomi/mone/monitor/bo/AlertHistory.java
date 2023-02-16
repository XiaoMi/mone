package com.xiaomi.mone.monitor.bo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 *
 * @author zhanggaofeng1
 */
@ToString
@Data
public class AlertHistory {

    private String id;
    private String alertId;
    private String alertName;
    private String alertCName;
    private Long alertDate;
    private String alertLevel;
    private String alertApp;
    private String alertAppId;
    private String alertIntance;
    private String alertContent;
    private String alertStat;
    private Integer iamTreeId;
    private String durationTime;
    private List alertGroupList;
    private Long startTime;
    private Long endTime;
    private String methodName;
    private String alertIp;
    private String detailedUrl;

}
