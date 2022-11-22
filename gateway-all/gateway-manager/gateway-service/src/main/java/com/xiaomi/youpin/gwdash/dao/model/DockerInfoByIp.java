package com.xiaomi.youpin.gwdash.dao.model;


import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("mione_docker_info_IP")
@Data
public class DockerInfoByIp {
    @Id
    private long id;

    @Column
    private String ip;

    @Column
    private long pipelineId;

    @Column("project_id")
    private long projectId;

    @Column("project_name")
    private String projectName;

    @Column("env_id")
    private long envId;

    @Column("env_name")
    private String envName;
}
