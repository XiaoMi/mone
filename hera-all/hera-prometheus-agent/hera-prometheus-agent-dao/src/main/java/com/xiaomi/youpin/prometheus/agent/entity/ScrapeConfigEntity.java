package com.xiaomi.youpin.prometheus.agent.entity;

import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
@Table("scrape_config")
@Data
public class ScrapeConfigEntity implements Serializable {
    @Id
    private Long Id;

    @Column("prom_cluster")
    private String PromCluster;

    @Column("region")
    private String Region;

    @Column("zone")
    private String Zone;

    @Column("env")
    private String Env;

    @Column("status")
    private String Status;

    @Column("instances")
    private String Instances;

    @Column("job_name")
    private String JobName;

    @Column("body")
    private String Body;

    @Column("created_by")
    private String CreatedBy;

    @Column("created_time")
    private Date CreateTime;

    @Column("updated_time")
    private Date UpdateTime;

    @Column("deleted_by")
    private String DeletedBy;

    @Column("deleted_time")
    private Date DeletedTime;

}
