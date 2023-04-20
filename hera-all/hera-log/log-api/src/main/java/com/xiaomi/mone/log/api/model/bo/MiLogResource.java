package com.xiaomi.mone.log.api.model.bo;

import com.xiaomi.mone.log.api.model.vo.EsIndexVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/5/10 17:52
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiLogResource implements Serializable {
    private Long id;
    private Integer operateCode;
    private Integer resourceCode;
    private String alias;
    private String clusterName;
    private String regionEn;
    private String conWay;
    private String serviceUrl;
    private String ak;
    private String sk;
    private String brokerName;
    private String orgId;
    private String teamId;
    private Integer isDefault;
    private String esToken;
    private String catalog;
    private String database;
    private List<String> labels = new ArrayList<>(0);
    private List<EsIndexVo> multipleEsIndex = new ArrayList<>();
}
