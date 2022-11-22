package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("mione_project_api")
public class ProjectApi {
    @Id
    private long id;

    @Column("project_id")
    private long projectId;

    @Column("api_id")
    private long apiId;

    private String jarUrl;
    private String entryClassName;
    private Integer groupId;
}
