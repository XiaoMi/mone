package com.xiaomi.mone.monitor.service.model.prometheus;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author gaoxihui
 * @date 2021/9/15 6:45 下午
 */
@Data
public class AlarmRuleQuery implements Serializable {
    private Integer id;

    private String alert;

    private String cname;

    private Integer metricType;

    private String forTime;

    private String annotations;

    private String ruleGroup;

    private String priority;

    private String env;

    private String op;

    private Float value;

    private Integer dataCount;

    private String sendInterval;

    private Integer iamId;

    private Integer templateId;

    private Integer ruleType;

    private Integer ruleStatus;

    private String remark;

    private String creater;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private String expr;

    private String labels;

    private String alertTeam;
}
