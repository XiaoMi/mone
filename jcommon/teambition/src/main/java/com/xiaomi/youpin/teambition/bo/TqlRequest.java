package com.xiaomi.youpin.teambition.bo;

import lombok.Data;

@Data
public class TqlRequest {
    private String tql;
    private int pageSize;
    private String pageToken;
    private String orderBy;
    //private TqlParam param;
}
