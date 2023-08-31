package com.xiaomi.mone.monitor.bo;


import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class RulePromQLTemplateInfo {

    private int id;
    private String name;
    private String promql;
    private Integer type;
    private String remark;
    private String creater;
    private Integer status;
    private long createTime;
    private long updateTime;

}
