package com.xiaomi.mone.app.api.model;

import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/5/5 9:54 AM
 */
@Data
@ToString
public class HeraMetaDataMessage {
    private Integer metaId;

    private String metaName;

    private Integer envId;

    private String envName;

    private String type;

    private String host;

    private HeraMetaDataPortModel port;

    /**
     * insert、update
     */
    private String operator;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;
}
