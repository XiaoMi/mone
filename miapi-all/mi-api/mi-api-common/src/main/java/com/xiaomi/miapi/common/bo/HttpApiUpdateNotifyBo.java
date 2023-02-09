package com.xiaomi.miapi.common.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HttpApiUpdateNotifyBo implements Serializable {
    private String env;
    private String apiController;
    private String ip;
    private Integer port;
    private String opUsername = "auto_update";
    private String updateMsg = "";
}
