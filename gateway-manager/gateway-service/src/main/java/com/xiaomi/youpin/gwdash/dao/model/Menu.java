package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Data
@Table("mione_menu")
public class Menu {

    @Id
    private long id;

    @Column
    private int priority;

    @Column
    private String role;

    @Column
    private String menu;

    @Column
    private int version;

    @Column
    private long ctime;

    @Column
    private long utime;
}
