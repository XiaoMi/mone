package com.xiaomi.mone.monitor.dao.model;


import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@ToString
@Table("rule_promql_template")
@Data
public class RulePromQLTemplate {

    @Id
    private int id;

    @Column
    private String name;

    @Column
    private String promql;

    @Column(value = "type", wrap = true)
    private Integer type;

    @Column
    private String remark;

    @Column
    private String creater;

    @Column(value = "status", wrap = true)
    private Integer status;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

}
