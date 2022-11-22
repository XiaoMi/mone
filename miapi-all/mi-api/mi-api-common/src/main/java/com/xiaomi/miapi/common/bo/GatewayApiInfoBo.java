//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class GatewayApiInfoBo implements Serializable {
    private String name;
    private String description;
    private String url;
    private String httpMethod;
    private String path;
    private Integer routeType;
    private String apiEnv;
    private String serviceName;
    private String methodName;
    private String serviceGroup;
    private String serviceVersion;
    private Integer apiNoteType;//接口详细说明文本类型
    private String apiRemark;//详细说明富文本
    private String apiDesc;//详细说明Markdown
    private Integer status;
    private Integer userId;
    private String updater;
    private String contentType;
    private Integer invokeLimit;
    private Integer qpsLimit;
    private Integer timeout;
    private String application;
    private String paramTemplate;
    private Boolean allowMock;
    private Integer projectId;
    private Integer groupId;
    private Integer apiRequestParamType;//接口请求类型 0：form-data  1：form-json 2：raw
    private String apiRequestRaw;//接口请求源数据
    private Integer apiResponseParamType;//接口请求类型  1:json 2：raw
    private String apiResponseRaw;//接口返回源数据
    private String updateMsg;
}
