package com.xiaomi.data.push.micloud.bo.response;

import java.util.List;

@lombok.Data
public class Data {
    private String provider;
    private List<String> ips;
    private String status;
    private String sequence;
    private List<Host> hosts;
}
