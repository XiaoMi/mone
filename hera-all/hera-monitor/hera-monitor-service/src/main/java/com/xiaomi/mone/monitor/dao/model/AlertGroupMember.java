package com.xiaomi.mone.monitor.dao.model;


import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

@ToString
@Table("alert_group_member")
@Data
public class AlertGroupMember {

    @Id
    private long id;

    @Column(value = "member")
    private String member;

    @Column(value = "member_id")
    private Long memberId;

    @Column(value = "alert_group_id")
    private Long alertGroupId;

    @Column
    private String creater;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Column
    private Integer deleted;

}
