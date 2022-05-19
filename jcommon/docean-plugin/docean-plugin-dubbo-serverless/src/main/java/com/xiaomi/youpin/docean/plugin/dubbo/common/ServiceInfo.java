package com.xiaomi.youpin.docean.plugin.dubbo.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2022/3/25 15:50
 */
@Data
public class ServiceInfo {

    private String name;

    private String group;

    private String version;

    private Class<?> interfaceClass;

    private int timeout;

    private boolean async;


}
