package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 *
 * @author zhanggaofeng1
 */
@Data
public class AlertHistoryParam {

    private Long startTime;
    private Long endTime;
    private Integer page;
    private Integer pageSize;
    private String projectId;
    private Integer iamTreeId;
    private String alertLevel;
    private String serverIp;
    private String instance;
    private String methodName;
    private String id;
    private String alertName;
    private String comment;
    private String alertStat = "firing";

    public void pageQryInit() {
        if (page == null || page <= 0) {
            page = 1;
        }
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        if (pageSize >= 100) {
            pageSize = 99;
        }
        if (endTime == null) {
            endTime = System.currentTimeMillis();
        }
        if (startTime == null) {
            startTime = endTime - 24L * 3600L * 1000L;
        }
    }

}
