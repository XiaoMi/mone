package com.xiaomi.data.push.micloud.bo.response;

import lombok.Data;

@Data
public class CatalystResponse {
    private int status;
    private String msg;
    private com.xiaomi.data.push.micloud.bo.response.Data data;
}
