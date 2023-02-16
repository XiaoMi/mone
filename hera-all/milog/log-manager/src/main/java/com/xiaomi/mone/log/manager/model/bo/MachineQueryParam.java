package com.xiaomi.mone.log.manager.model.bo;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/7/21 17:24
 */
@Data
public class MachineQueryParam {
    private Long id;
    private String ip;
    private Integer type;
    private Integer pageSize;
    private Integer pageNum;
}
