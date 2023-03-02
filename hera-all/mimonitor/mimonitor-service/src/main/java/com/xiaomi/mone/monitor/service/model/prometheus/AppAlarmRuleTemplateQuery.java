package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AppAlarmRuleTemplateQuery implements Serializable {

    private Integer id;

    private String name;

    private Integer type;

    private String remark;

    private String creater;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private Integer page;

    private Integer pageSize;

    private Integer strategyType;


}