package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.util.List;

@Data
public class BatchImportHttpApiBo {
    private String httpModuleClassName;
    private List<String> apiNames;
    private Integer projectID;
    private Integer groupID;
    private String ip;
    private Integer port;
    //是否强制更新
    private Boolean forceUpdate;
    private String updateUserName;
}
