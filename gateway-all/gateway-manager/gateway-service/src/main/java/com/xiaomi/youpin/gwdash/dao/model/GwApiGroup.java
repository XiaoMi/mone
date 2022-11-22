package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("api_group_info")
@Data
public class GwApiGroup {
    @Id
    private Integer id;

    @Column
    private Integer gid;

    @Column
    private String name;

    @Column
    private String description;

    @Column("base_url")
    private String baseUrl;

    @Column
    private Long ctime;

    @Column
    private Long utime;
}
