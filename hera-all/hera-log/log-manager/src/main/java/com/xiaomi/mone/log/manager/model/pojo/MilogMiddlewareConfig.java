package com.xiaomi.mone.log.manager.model.pojo;

import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.vo.ResourceInfo;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
import lombok.Data;
import org.nutz.dao.entity.annotation.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description 中间件配置
 * @date 2021/9/17 15:51
 */
@Table("milog_middleware_config")
@Comment("milog配置中间件配置")
@Data
public class MilogMiddlewareConfig extends BaseCommon implements Serializable {

    @Id
    @Comment("主键Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "type")
    @ColDefine(customType = "smallint")
    @Comment("类型  MiddlewareEnum.code")
    public Integer type;

    @Column(value = "region_en")
    @ColDefine(type = ColType.VARCHAR, width = 40)
    @Comment("类型  MachineRegionEnum.en")
    private String regionEn;

    @Column(value = "alias")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("别名")
    public String alias;

    @Column(value = "name_server")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("nameServer地址")
    private String nameServer;

    @Column(value = "service_url")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("nameServer地址")
    private String serviceUrl;

    @Column(value = "ak")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    @Comment("ak")
    private String ak;

    @Column(value = "sk")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    @Comment("sk")
    private String sk;

    @Column(value = "broker_name")
    @ColDefine(type = ColType.VARCHAR, width = 200)
    @Comment("broker_name")
    private String brokerName;

    @Column(value = "authorization")
    @ColDefine(type = ColType.VARCHAR, width = 1024)
    @Comment("授权信息")
    private String authorization;

    @Column(value = "org_id")
    @ColDefine(type = ColType.INT, width = 20)
    @Comment("团队Id")
    private String orgId;

    @Column(value = "team_id")
    @ColDefine(type = ColType.INT, width = 20)
    @Comment("用户组ID")
    private String teamId;

    @Column(value = "is_default")
    @ColDefine(type = ColType.INT, width = 20)
    @Comment("是否默认当不选择mq的时候采用这个配置(1.是 0.否)")
    private Integer isDefault;

    @Column(value = "labels")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("标签列表")
    private List<String> labels;

    @Column(value = "token")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @Comment("token")
    private String token;

    @Column(value = "dt_catalog")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @Comment("catalog")
    private String dtCatalog;

    @Column(value = "dt_database")
    @ColDefine(type = ColType.VARCHAR, width = 255)
    @Comment("database")
    private String dtDatabase;

    private String clusterName;


    public ResourceInfo configToResourceVO() {
        return ResourceInfo.builder().id(this.id)
                .alias(this.alias)
                .regionEn(this.regionEn)
                .regionCn(CommonExtensionServiceFactory.getCommonExtensionService().getMachineRoomName(this.regionEn))
                .clusterName(this.nameServer)
                .serviceUrl(this.serviceUrl)
                .brokerName(this.brokerName)
                .ak(this.ak)
                .sk(this.sk)
                .orgId(this.orgId)
                .teamId(this.teamId)
                .labels(this.labels)
                .ctime(this.getCtime())
                .utime(this.getUtime())
                .creator(this.getCreator())
                .updater(this.getUpdater()).build();

    }

    public static MilogMiddlewareConfig miLogMqResourceToConfig(MiLogResource resource) {
        MilogMiddlewareConfig milogMiddlewareConfig = new MilogMiddlewareConfig();
        milogMiddlewareConfig.setAlias(resource.getAlias());
        milogMiddlewareConfig.setType(resource.getResourceCode());
        milogMiddlewareConfig.setRegionEn(resource.getRegionEn());
        milogMiddlewareConfig.setNameServer(resource.getClusterName());
        milogMiddlewareConfig.setServiceUrl(resource.getServiceUrl());
        milogMiddlewareConfig.setAk(resource.getAk());
        milogMiddlewareConfig.setSk(resource.getSk());
        milogMiddlewareConfig.setOrgId(resource.getOrgId());
        milogMiddlewareConfig.setTeamId(resource.getTeamId());
        milogMiddlewareConfig.setBrokerName(resource.getBrokerName());
        milogMiddlewareConfig.setLabels(resource.getLabels());
        milogMiddlewareConfig.setIsDefault(resource.getIsDefault());
        return milogMiddlewareConfig;
    }

}
