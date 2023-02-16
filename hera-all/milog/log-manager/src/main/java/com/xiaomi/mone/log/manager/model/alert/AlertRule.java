package com.xiaomi.mone.log.manager.model.alert;

import lombok.Data;
import org.nutz.dao.entity.annotation.*;

@Table("alert_rule")
@Data
public class AlertRule {

    @Id
    private long id;

    @Column
    private String name;

    @Column("alert_id")
    private long alertId;

    @Column
    private String regex;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private String creator;
}
