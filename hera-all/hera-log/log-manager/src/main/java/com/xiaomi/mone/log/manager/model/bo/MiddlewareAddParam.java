package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:57
 */
@Data
public class MiddlewareAddParam implements Serializable {
    /**
     * com.xiaomi.mone.log.api.enums.MiddlewareEnum.code
     */
    private Integer type;

    private String regionEn;

    private List<?> types;

    private String alias;

    private String nameServer;

    private String serviceUrl;

    private String ak;

    private String sk;

    private String authorization;

    private String orgId;

    private String teamId;

    private Integer isDefault = 0;

}
