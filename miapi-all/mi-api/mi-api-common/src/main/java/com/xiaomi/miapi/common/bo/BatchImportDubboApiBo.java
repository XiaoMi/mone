package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.util.List;

@Data
public class BatchImportDubboApiBo {
    private String moduleClassName;
    private List<String> apiNames;
    private String env;
    private Integer projectID;
    private Integer groupID;
    private String ip;
    private Integer port;
    //是否强制更新
    private Boolean forceUpdate;
    private String updateUserName;
}
