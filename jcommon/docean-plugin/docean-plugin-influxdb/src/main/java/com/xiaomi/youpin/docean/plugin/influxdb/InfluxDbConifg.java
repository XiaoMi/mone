package com.xiaomi.youpin.docean.plugin.influxdb;

import lombok.Data;

/**
 * @author zhangjunyi
 * created on 2020/8/14 3:54 下午
 */
@Data
public class InfluxDbConifg {
    private String dbUrl;
    private String username;
    private String password;
    private String databaseName;
    private String retentionPolicy;
}
