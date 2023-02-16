package com.xiaomi.mone.monitor.bo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserInfo {
    private long id;
    private String name;
    private String cname;
    private String email;
    private Integer type;
}
