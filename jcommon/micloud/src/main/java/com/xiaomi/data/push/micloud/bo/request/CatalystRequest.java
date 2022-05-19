package com.xiaomi.data.push.micloud.bo.request;

import lombok.Data;

import java.util.List;

@Data
public class CatalystRequest {
    private String provider;
    private List<String> ips;
}
