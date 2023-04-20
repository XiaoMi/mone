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
public class MiddlewareQueryParam extends MilogPageParam implements Serializable {
    /**
     * com.xiaomi.mone.log.api.enums.MiddlewareEnum
     */
    private List<?> types;

    private String alias;

    private String regionEn;

}
