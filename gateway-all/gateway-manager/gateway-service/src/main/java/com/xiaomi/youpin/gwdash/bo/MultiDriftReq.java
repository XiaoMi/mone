package com.xiaomi.youpin.gwdash.bo;

import lombok.Data;

import java.util.List;

@Data
public class MultiDriftReq {
    private String ip;
    private String targetIp;
    private List<Long> envIds;
}
