package com.xiaomi.mone.monitor.dao.model;


import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@ToString
@Table("app_alarm_strategy")
@Data
public class AlarmStrategy {

    @Id
    private int id;

    @Column
    private Integer appId;

    @Column
    private Integer iamId;

    @Column
    private String appName;

    @Column("strategy_type")
    private Integer strategyType;

    @Column("strategy_name")
    private String strategyName;

    @Column(value = "desc", wrap = true)
    private String desc;

    @Column
    private String creater;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Column(value = "status", wrap = true)
    private Integer status;

    @Column("alert_team")
    private String alertTeam;

    @Column
    private String group3;

    @Column
    private String group4;

    @Column
    private String group5;

    @Column
    private String envs;

    @Column("alert_members")
    private String alertMembers;

    @Column("at_members")
    private String atMembers;

}
