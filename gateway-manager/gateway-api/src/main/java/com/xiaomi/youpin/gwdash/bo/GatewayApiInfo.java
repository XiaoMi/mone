package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GatewayApiInfo implements Serializable {
    private Long id;

    private String name;

    private String description;

    private String url;

    private String httpMethod;

    private String path;

    private Integer routeType;

    private String serviceName;

    private String methodName;

    private String serviceGroup;

    private String serviceVersion;

    private Integer status;

    private String creator;

    private String contentType;

    private Integer invokeLimit;

    private Integer qpsLimit;

    private Integer timeout;

    private String application;

    private String paramTemplate;

    private boolean allowMock;

    private String mockData;

    private String mockDataDesc;

}
