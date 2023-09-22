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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.xiaomi.mone.log.api.enums.MiddlewareEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogResource;
import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import com.xiaomi.mone.log.api.model.vo.ResourceInfo;
import com.xiaomi.mone.log.manager.model.BaseCommon;
import com.xiaomi.mone.log.manager.service.extension.common.CommonExtensionServiceFactory;
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
public class MilogEsClusterDO extends BaseCommon implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Cluster type
     */
    private String tag;

    /**
     * Cluster name
     */
    private String name;

    /**
     * Cluster name
     */
    private String clusterName;

    /**
     * room
     */
    private String region;

    /**
     * area
     */
    private String area;

    /**
     * ES address
     */
    private String addr;

    /**
     * ES username
     */
    private String user;

    /**
     * ES password
     */
    private String pwd;

    private String token;

    private String dtCatalog;

    private String dtDatabase;

    private String conWay;

    private Integer isDefault;

    @TableField(typeHandler = FastjsonTypeHandler.class)
    private List<String> labels;

    public static MilogMiddlewareConfig miLogEsResourceToConfig(MilogEsClusterDO resource) {
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
        milogMiddlewareConfig.setIsDefault(resource.getIsDefault());
        return milogMiddlewareConfig;
    }


    public ResourceInfo configToResourceVO(List<EsIndexVo> multipleEsIndex) {
        return ResourceInfo.builder().id(this.id)
                .alias(this.name)
                .clusterName(this.clusterName)
                .regionEn(this.area)
                .regionCn(CommonExtensionServiceFactory.getCommonExtensionService().getMachineRoomName(this.area))
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

    public static MilogEsClusterDO miLogEsResourceToConfig(MiLogResource resource) {
        MilogEsClusterDO clusterDO = new MilogEsClusterDO();
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
