package com.xiaomi.mone.log.manager.model.alert;


import lombok.Data;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("alert_log")
@Data
public class AlertLog {

    @Id
    private long id;

    @Column("alert_id")
    private long alertId;

    @Column("app_name")
    private String appName;

    @Column("start_time")
    private long startTime;

    @Column("end_time")
    private long endTime;

    @Column
    private String ip;

    @Column("alert_count")
    private int alertCount;

    @Column("alert_level")
    private String alertLevel;

    @Column("log_path")
    private String logPath;

    @Column
    private String content;

    @Column
    private long ctime;

    @Column
    private long utime;
}
