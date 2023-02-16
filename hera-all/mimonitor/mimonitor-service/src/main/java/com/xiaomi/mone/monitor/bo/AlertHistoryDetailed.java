package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 *
 * @author zhanggaofeng1
 */
@Data
public class AlertHistoryDetailed {

    private String id;
    private String alertLevel;
    private String alertStat;
    private String alertApp;
    private String alertAppId;
    private String alertIp;
    private String alertIntance;
    private String alertContent;
    private Long alertStartTime;
    private Long alertEndTime;
    private Integer iamTreeId;
    private String alertName;
    private String alertCName;
    private String alertId;
    private Long alertDate;
    private String durationTime;

}
