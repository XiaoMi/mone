package com.xiaomi.mone.monitor.dao.model;


import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;
import java.util.List;

@ToString
@Table("alert_group")
@Data
public class AlertGroup {

    @Id
    private long id;

    @Column
    private String name;

    @Column(value = "desc", wrap = true)
    private String desc;

    @Column("chat_id")
    private String chatId;

    @Column
    private String creater;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Column(value = "type", wrap = true)
    private String type;

    @Column(value = "rel_id")
    private Long relId;

    @Column
    private Integer deleted;

    @Many(target = AlertGroupMember.class, field = "alertGroupId")
    private List<AlertGroupMember> members;

}
