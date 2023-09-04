package com.xiaomi.mone.monitor.bo;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @date 2023-02-23
 */
@Data
public class GrafanaCreateDataSourceRes {
    private GrafanaCreateDataSourceInnerRes datasource;
    private int id;
    private String message;
    private String name;
}
