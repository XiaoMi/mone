package com.xiaomi.youpin.tesla.traffic.recording.api.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonEnum implements Serializable {
    private int code;
    private String desc;

    public CommonEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
