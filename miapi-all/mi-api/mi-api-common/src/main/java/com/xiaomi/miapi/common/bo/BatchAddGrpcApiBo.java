package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BatchAddGrpcApiBo implements Serializable {
    private List<GrpcServiceMethod> serviceMethods;
    private String env;
    private String appName;
    private String symbol;
    private String ip;
    private Integer port;
    private Integer projectID;
    //是否强制更新
    private Boolean forceUpdate;
    private String updateUserName;
}
