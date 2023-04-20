package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 11:59
 */
@Data
public class MilogPageParam implements Serializable {

    private Integer pageSize = 20;

    private Integer page = 1;

}
