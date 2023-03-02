package com.xiaomi.miapi.bo;

import lombok.Data;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class HttpTestBo {
    private String method;
    private String url;
    private Integer timeout;
    private String headers;
    private String body;
    private Boolean useX5Filter;
    private String appID;
    private String appkey;
    private String x5Method;
}
