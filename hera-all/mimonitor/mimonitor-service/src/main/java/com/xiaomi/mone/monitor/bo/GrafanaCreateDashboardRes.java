package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-03-05
 */
@Data
public class GrafanaCreateDashboardRes {
    private int id;
    private String uid;
    private String url;
    private String status;
    private int version;
    private String slug;
    private String message;
}
