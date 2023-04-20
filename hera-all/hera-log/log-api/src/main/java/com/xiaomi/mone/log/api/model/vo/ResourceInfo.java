package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/10 11:25
 */
@Data
@AllArgsConstructor
@SuperBuilder
public class ResourceInfo extends CommonVo implements Serializable {
    private Long id;
    private String alias;
    private String regionEn;
    private String regionCn;
    private String serviceUrl;
    private String ak;
    private String sk;
    private String orgId;
    private String teamId;
    private String clusterName;
    private String brokerName;
    private String esToken;
    private String conWay;
    private String catalog;
    private String database;
    private List<String> labels;
    private List<EsIndexVo> multipleEsIndex;

}
