package com.xiaomi.youpin.docean.plugin.dmesh.service;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class MeshServiceConfig implements Serializable {


    private String serviceName;

    private String methodName;

    private String group;

    private String version;

    private String app;

}
