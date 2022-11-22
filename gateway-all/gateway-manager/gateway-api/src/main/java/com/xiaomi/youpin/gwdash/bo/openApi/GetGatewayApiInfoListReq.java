package com.xiaomi.youpin.gwdash.bo.openApi;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetGatewayApiInfoListReq implements Serializable {
    private int page;

    private int pageSize;

    private String url;

    private String name;
}
