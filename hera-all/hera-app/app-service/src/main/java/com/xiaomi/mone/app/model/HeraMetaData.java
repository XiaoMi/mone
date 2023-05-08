package com.xiaomi.mone.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.xiaomi.mone.app.api.model.HeraMetaDataType;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/28 10:04 AM
 */
@Data
@TableName(value = "hera_meta_data", autoResultMap = true)
@ToString
public class HeraMetaData {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 元数据id，比如appId
     */
    private Integer metaId;

    /**
     * 元数据的名称，app类型就是appName，mysql类型就是DBA定义的DBName等等
     */
    private String metaName;

    /**
     * 元数据类型，有APP、MYSQL、REDIS、ES、MQ等，具体可以参照{@link HeraMetaDataType}
     */
    private String type;

    /**
     *元数据的实例，有可能是IP，有可能是域名，也有可能是hostName
     */
    private String host;

    /**
     *该元数据暴露的端口
     */
    @TableField(value = "port", typeHandler = JacksonTypeHandler.class)
    private HeraMetaDataPort port;

    private Date createTime;

    private Date updateTime;

    private String createBy;

    private String updateBy;

    public HeraMetaData(){}

    public HeraMetaData(Long id, Integer metaId, String metaName, String type, String host, HeraMetaDataPort port, Date createTime, Date updateTime, String createBy, String updateBy) {
        this.id = id;
        this.metaId = metaId;
        this.metaName = metaName;
        this.type = type;
        this.host = host;
        this.port = port;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.createBy = createBy;
        this.updateBy = updateBy;
    }
}
