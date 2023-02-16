package com.xiaomi.mone.monitor.dao.model;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

import java.util.Date;

@ToString
@Table("hera_oper_log")
@Data
public class HeraOperLog {

    @Id
    private Integer id;

    @Column(value = "oper_name")
    private String operName;

    @Column(value = "log_type")
    private Integer logType;

    @Column(value = "before_parent_id")
    private Integer beforeParentId;

    @Column(value = "after_parent_id")
    private Integer afterParentId;

    @Column(value = "module_name")
    private String moduleName;

    @Column(value = "interface_name")
    private String interfaceName;

    @Column(value = "interface_url")
    private String interfaceUrl;

    @Column(value = "action")
    private String action;

    @Column(value = "before_data")
    private String beforeData;

    @Column(value = "after_data")
    private String afterData;

    @Column("create_time")
    private Date createTime;

    @Column("update_time")
    private Date updateTime;

    @Column(value = "data_type")
    private Integer dataType;

    @Column(value = "result_desc")
    private String resultDesc;

}
