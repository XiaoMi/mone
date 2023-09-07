/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
 * @description Middleware configuration
 * @date 2021/9/17 15:51
 */
@Table("milog_middleware_config")
@Comment("Milog configures middleware configuration")
@Data
public class MilogMiddlewareConfig extends BaseCommon implements Serializable {

    @Id
    @Comment("Primary key Id")
    @ColDefine(customType = "bigint")
    private Long id;

    @Column(value = "type")
    @ColDefine(customType = "smallint")
    @Comment("type  MiddlewareEnum.code")
    public Integer type;

    @Column(value = "region_en")
    @ColDefine(type = ColType.VARCHAR, width = 40)
    @Comment("type  MachineRegionEnum.en")
    private String regionEn;

    @Column(value = "alias")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("alias")
    public String alias;

    @Column(value = "name_server")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("nameServer address")
    private String nameServer;

    @Column(value = "service_url")
    @ColDefine(type = ColType.VARCHAR, width = 128)
    @Comment("nameServer address")
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
    @Comment("Authorization Information")
    private String authorization;

    @Column(value = "org_id")
    @ColDefine(type = ColType.INT, width = 20)
    @Comment("team Id")
    private String orgId;

    @Column(value = "team_id")
    @ColDefine(type = ColType.INT, width = 20)
    @Comment("user group ID")
    private String teamId;

    @Column(value = "is_default")
    @ColDefine(type = ColType.INT, width = 20)
    @Comment("Whether to default this configuration when mq is not selected (1.Yes 0.No)")
    private Integer isDefault;

    @Column(value = "labels")
    @ColDefine(type = ColType.MYSQL_JSON)
    @Comment("A list of tags")
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
