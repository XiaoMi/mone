package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("mione_custom_config")
@Data
public class TCustomeConfig {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private String content;

    @Column
    private long utime;

    @Column
    private long ctime;

    @Column
    private int version;
}
