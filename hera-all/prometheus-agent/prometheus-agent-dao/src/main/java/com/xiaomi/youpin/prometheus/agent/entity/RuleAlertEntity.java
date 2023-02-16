package com.xiaomi.youpin.prometheus.agent.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@Table("alert")
@Data
public class RuleAlertEntity implements Serializable {
    @Id
    private Long id;

    @Column("name")
    private String name;

    @Column("cname")
    private String cname;

    @Column("expr")
    private String expr;

    @Column("labels")
    private String labels;

    @Column("annotations")
    private String annotation;

    @Column("alert_for")
    private String alertFor;

    @Column("enabled")
    private int enabled;

    @Column("env")
    private String env;

    @Column("priority")
    private int priority;

    @Column("created_by")
    private String createdBy;

    @Column("created_time")
    private Date createdTime;

    @Column("updated_time")
    private Date updatedTime;

    @Column("deleted_by")
    private String deletedBy;

    @Column("deleted_time")
    private Date deletedTime;

    @Column("prom_cluster")
    private String promCluster;

    @Column("status")
    private String status;

    @Column("instances")
    private String instances;

    @Column("thresholds_op")
    private String thresholdsOp;

    @Column("thresholds")
    private String thresholds;

    @Column("type")
    private String type;

    @Column("alert_member")
    private String alertMember;

    @Column("alert_at_people")
    private String alertAtPeople;

    @Column("alert_group")
    private String alert_group;
}
