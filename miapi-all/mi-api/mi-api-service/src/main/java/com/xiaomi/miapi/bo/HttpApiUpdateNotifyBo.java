package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class HttpApiUpdateNotifyBo implements Serializable {
    private String env;
    private String apiController;
    private String ip;
    private Integer port;
    private String opUsername = "auto_update";
    private String updateMsg = "";
}
