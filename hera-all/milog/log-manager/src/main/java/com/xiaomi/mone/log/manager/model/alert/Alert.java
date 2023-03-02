package com.xiaomi.mone.log.manager.model.alert;


import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Table("alert")
@Data
public class Alert {

    @Id
    private long id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String app;

    @Column("app_name")
    private String appName;

    @Column("milog_app_id")
    private Long milogAppId;

    //app and log_path are used to get ak,sk,consumer server, consumer topic
    @Column("log_path")
    private String logPath;

    @Column
    private String contacts;

    @Column("feishu_groups")
    private String feishuGroups;

    @Column("arguments")
    @ColDefine(type = ColType.MYSQL_JSON)
    private Map<String, String> arguments;

    @Column("flink_job_name")
    private String flinkJobName;

    @Column("flink_cluster")
    private String flinkCluster;

    @Column("job_id")
    private Long jobId;

    @Column
    private long ctime;

    @Column
    private long utime;

    @Column
    private int status;

    @Column
    private String creator;


    public void addArgument(String key, String value) {
        if (arguments == null) {
            arguments = new HashMap<>();
        }
        arguments.put(key, value);
    }

    public String getArgument(String key) {
        if (arguments == null) {
            return null;
        }
        return arguments.get(key);
    }
}
