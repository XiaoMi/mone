package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class DubboApiUpdateNotifyBo implements Serializable {
    private String env;
    private String moduleClassName;
    private String ip;
    private Integer port;
    private String opUsername = "auto_update";
    private String updateMsg = "";
}
