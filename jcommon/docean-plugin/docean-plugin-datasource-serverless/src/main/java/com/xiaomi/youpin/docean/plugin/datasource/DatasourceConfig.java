package com.xiaomi.youpin.docean.plugin.datasource;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2020/6/27
 */
@Data
public class DatasourceConfig {

    /**
     * 数据源的名称
     */
    private String name;

    private String driverClass;

    private Integer defaultInitialPoolSize;

    private Integer defaultMaxPoolSize;

    private Integer defaultMinPoolSize;

    private String dataSourceUrl;

    private String dataSourceUserName;

    private String dataSourcePasswd;
}
