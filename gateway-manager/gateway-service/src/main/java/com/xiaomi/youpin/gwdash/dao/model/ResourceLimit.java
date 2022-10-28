package com.xiaomi.youpin.gwdash.dao.model;


import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("mione_resource_limit")
public class ResourceLimit {
    @Id
    private long id;

    @Column("env_id")
    private long envId;

    @Column
    private long cpu;

    @Column
    private long memery;

    @Column
    private long replicate;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column(version = true)
    private int version;
}
