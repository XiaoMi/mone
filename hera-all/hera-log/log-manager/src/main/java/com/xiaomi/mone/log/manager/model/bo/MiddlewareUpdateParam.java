package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:57
 */
@Data
public class MiddlewareUpdateParam extends MiddlewareAddParam implements Serializable {
    private Long id;
}
