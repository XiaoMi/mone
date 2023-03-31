package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class AuthAccountVo {
    private String name;
    private String url;
    private String icon;
    private String desc;
}
