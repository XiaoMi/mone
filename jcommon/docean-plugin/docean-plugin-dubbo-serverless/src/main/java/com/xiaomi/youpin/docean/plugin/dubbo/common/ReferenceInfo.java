package com.xiaomi.youpin.docean.plugin.dubbo.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2022/3/25 15:35
 */
@Data
public class ReferenceInfo {

    private String name;

    private Class<?> interfaceClass;

    private String group;

    private boolean check;

    private int timeout;

    /**
     * Cluster strategy, legal values include: failover, failfast, failsafe, failback, forking
     */
    private String cluster;

    private String version;


}
