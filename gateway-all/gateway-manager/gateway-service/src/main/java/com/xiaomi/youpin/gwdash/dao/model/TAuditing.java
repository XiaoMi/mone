package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.Map;

@Table("mione_auditing")
@Data
public class TAuditing {

    @Id
    private long id;

    @Column
    private String operator;

    @Column
    private String type;

    @Column
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> attachment;

    @Column
    private int version;

    @Column
    private long ctime;

    @Column
    private long utime;
}
