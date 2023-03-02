//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
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
    private Integer apiNoteType;
    private String apiRemark;
    private String apiDesc;
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
    //req param type 0：form-data  1：form-json 2：raw
    private Integer apiRequestParamType;
    private String apiRequestRaw;
    // 1:json 2：raw
    private Integer apiResponseParamType;
    private String apiResponseRaw;
    private String updateMsg;
}
