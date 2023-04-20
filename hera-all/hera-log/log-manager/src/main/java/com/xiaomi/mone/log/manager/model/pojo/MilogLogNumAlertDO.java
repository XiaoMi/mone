package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("milog_log_num_alert")
public class MilogLogNumAlertDO {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String day;

    private Long number;

    private Long appId;

    private String appName;

    private String alertUser;

    private Long ctime;

    public MilogLogNumAlertDO(String day, Long number, Long appId, String appName, String alertUser, Long ctime) {
        this.day = day;
        this.number = number;
        this.appId = appId;
        this.appName = appName;
        this.alertUser = alertUser;
        this.ctime = ctime;
    }
}
