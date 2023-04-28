package com.xiaomi.mone.app.api.model;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/28 10:04 AM
 */
public class HeraMetaData {
    /**
     * 元数据的名称，app类型就是appName，mysql类型就是DBA定义的DBName等等
     */
    private String name;

    /**
     * 元数据类型，有APP、MYSQL、REDIS、ES、MQ等，具体可以参照{@link HeraMetaDataConst}
     */
    private String type;

    /**
     *有可能是IP，有可能是域名，也有可能是hostName
     */
    private String host;

    /**
     *该元数据暴露的端口
     */
    private String port;
}
