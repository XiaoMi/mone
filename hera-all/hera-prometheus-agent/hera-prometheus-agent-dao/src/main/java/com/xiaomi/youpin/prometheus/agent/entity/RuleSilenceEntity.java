package com.xiaomi.youpin.prometheus.agent.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@ToString(callSuper = true)
@Table("silence")
@Data
public class RuleSilenceEntity {
    @Id
    private Long id;

    @Column("name")
    private String uuid;

    @Column("comment")
    private String comment;

    @Column("start_time")
    private Date startTime;

    @Column("end_time")
    private Date endTime;

    @Column("created_time")
    private Date createdTime;

    @Column("updated_time")
    private Date updatedTime;

    @Column("prom_cluster")
    private String promCluster;

    @Column("status")
    private String status;

    @Column("alert_id")
    private String alertId;

}
