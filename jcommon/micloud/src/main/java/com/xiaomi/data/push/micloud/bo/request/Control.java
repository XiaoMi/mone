package com.xiaomi.data.push.micloud.bo.request;

import lombok.Data;

@Data
public class Control {
    private String reason;
    private String action;
    private String [] hostnames;
}
