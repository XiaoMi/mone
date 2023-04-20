package com.xiaomi.mone.log.manager.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/4/20 15:06
 */
@Data
@Builder
public class AccessMiLogVo {
    private Long spaceId;
    private Long storeId;
    private Long tailId;
    private String tailName;
}
