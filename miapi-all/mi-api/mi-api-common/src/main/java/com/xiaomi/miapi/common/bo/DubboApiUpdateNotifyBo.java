package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class DubboApiUpdateNotifyBo implements Serializable {
    private String env;
    private String moduleClassName;
    private String ip;
    private Integer port;
    private String opUsername = "auto_update";
    private String updateMsg = "";
}
