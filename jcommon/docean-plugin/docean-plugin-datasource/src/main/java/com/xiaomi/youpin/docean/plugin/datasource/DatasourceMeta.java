package com.xiaomi.youpin.docean.plugin.datasource;

import lombok.Data;

import java.io.Serializable;

@Data
public class DatasourceMeta implements Serializable {

    private int id;

    private int type;

    private String driverClass;

    private String dataSourceUrl;

    private String userName;

    private String passWd;

    private int poolSize;

    private int maxPoolSize;

    private int minPoolSize;

    private String name;

    private int state;

    private long ctime;

    private long utime;

    private String creator;

    private String jarPath;

    private String iocPackage;

    private String appName;

    private String regAddress;

    private String apiPackage;

    private int threads;

    private String redisType;

    private String nacosDataId;

    private String nacosGroup;

    private String mongoDatabase;

    private String description;

}
