package com.xiaomi.miapi.common.bo;

import lombok.Data;

/**
 * @Description TODO
 * @Author zhenxing.dong
 * @Date 2021/8/8 20:53
 */
@Data
public class GetDubboApiRequestBo {
    private String moduleClassName;
    private String apiName;
    private String ip;
    private Integer port;
}
