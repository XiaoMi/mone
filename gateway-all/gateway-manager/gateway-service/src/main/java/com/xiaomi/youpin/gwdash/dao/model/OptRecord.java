package com.xiaomi.youpin.gwdash.dao.model;

import lombok.Data;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.*;

import java.util.Date;

@Data
@Table("opt_record")
public class OptRecord {
    @Id
    private long id;

    @Column("client_ip")
    private String clientIp;

    @Column("opt_id")
    private String optId;

    @Column("resource_url")
    private String resourceUrl;

    @Column("resource_desc")
    private String resourceDesc;

    @Column("opt_time")
    private Date optTime;

    @Column("req_method")
    private String reqMethod;

    @Column("in_param")
    private String inParam;

    @Column("return_code")
    private String returnCode;

    @Column("out_param")
    private String outParam;

    @Column("duration")
    private Integer duration;


}