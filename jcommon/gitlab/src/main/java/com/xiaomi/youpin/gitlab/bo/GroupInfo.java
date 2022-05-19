package com.xiaomi.youpin.gitlab.bo;

import lombok.Data;

@Data
public class GroupInfo {
    private Integer id;
    private String kind;
    private String name;
    private String path;
}
