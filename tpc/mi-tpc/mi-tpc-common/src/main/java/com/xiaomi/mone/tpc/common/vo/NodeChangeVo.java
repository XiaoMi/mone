package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@ToString
@Data
public class NodeChangeVo {
    private long id;
    private int bindType = 1;
    private String appName;
    private String appLanguage = "java";
    private int platformType = 0;
    private int appType = 0;
    private int delete = 0;
    private List<String> joinedMembers;
    private Integer iamTreeId;
    private Integer iamTreeType;
    private Map<String, String> env;
}
