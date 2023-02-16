package com.xiaomi.mone.log.manager.model.alert;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("alert_condition")
@Data
public class AlertCondition {
    @Id
    private long id;

    @Column("alert_rule_id")
    private long alertRuleId;

    @Column
    private String operation;

    @Column
    private int value;

    @Column("alert_level")
    private String alertLevel;

    @Column
    private long period;

    @Column("sort_order")
    private long order;

    @Column("send_alert_time")
    private long sendAlertTime;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private String creator;
}
