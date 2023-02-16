package com.xiaomi.mone.log.manager.model.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.xiaomi.mone.log.api.enums.MachineRegionEnum;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import com.xiaomi.mone.log.api.model.vo.ResourceInfo;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wanghaoyang
 * @since 2021-09-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName(value = "milog_es_cluster", autoResultMap = true)
public class LogEsClusterDO extends BaseCommon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 集群类型
     */
    private String tag;

    /**
     * 集群名称
     */
    private String name;

    /**
     * 融合云上集群名
     */
    private String clusterName;

    /**
     * 机房
     */
    private String region;

    /**
     * 地区
     */
    private String area;

    /**
     * ES地址
     */
    private String addr;

    /**
     * ES用户名
     */
    private String user;

    /**
     * ES密码
     */
    private String pwd;

    private String token;

    private String dtCatalog;

    private String dtDatabase;

    private String conWay;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> labels;

    public static MilogMiddlewareConfig miLogEsResourceToConfig(LogEsClusterDO resource) {
        MilogMiddlewareConfig milogMiddlewareConfig = new MilogMiddlewareConfig();
        milogMiddlewareConfig.setId(resource.getId());
        milogMiddlewareConfig.setAlias(resource.getName());
        milogMiddlewareConfig.setClusterName(resource.clusterName);
        milogMiddlewareConfig.setType(MiddlewareEnum.ELASTICSEARCH.getCode());
        milogMiddlewareConfig.setRegionEn(resource.getArea());
        milogMiddlewareConfig.setServiceUrl(resource.getAddr());
        milogMiddlewareConfig.setAk(resource.getUser());
        milogMiddlewareConfig.setSk(resource.getPwd());
        milogMiddlewareConfig.setLabels(resource.getLabels());
        milogMiddlewareConfig.setCreator(resource.getCreator());
        milogMiddlewareConfig.setUpdater(resource.getUpdater());
        milogMiddlewareConfig.setUtime(resource.getUtime());
        milogMiddlewareConfig.setCtime(resource.getCtime());
        return milogMiddlewareConfig;
    }


    public ResourceInfo configToResourceVO(List<EsIndexVo> multipleEsIndex) {
        return ResourceInfo.builder().id(this.id)
                .alias(this.name)
                .clusterName(this.clusterName)
                .regionEn(this.area)
                .regionCn(MachineRegionEnum.queryCnByEn(this.area))
                .serviceUrl(this.addr)
                .ak(this.user)
                .sk(this.pwd)
                .labels(this.labels)
                .esToken(this.token)
                .conWay(this.conWay)
                .catalog(this.dtCatalog)
                .database(this.dtDatabase)
                .multipleEsIndex(multipleEsIndex)
                .ctime(this.getCtime())
                .utime(this.getUtime())
                .creator(this.getCreator())
                .updater(this.getUpdater()).build();
    }

    public static LogEsClusterDO miLogEsResourceToConfig(MiLogResource resource) {
        LogEsClusterDO clusterDO = new LogEsClusterDO();
        clusterDO.setName(resource.getAlias());
        clusterDO.setClusterName(resource.getClusterName());
        clusterDO.setArea(resource.getRegionEn());
        clusterDO.setAddr(resource.getServiceUrl());
        clusterDO.setUser(resource.getAk());
        clusterDO.setPwd(resource.getSk());
        clusterDO.setLabels(resource.getLabels());
        clusterDO.setConWay(resource.getConWay());
        clusterDO.setToken(resource.getEsToken());
        clusterDO.setDtCatalog(resource.getCatalog());
        clusterDO.setDtDatabase(resource.getDatabase());
        return clusterDO;
    }

}
