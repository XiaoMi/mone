package com.xiaomi.mone.log.manager.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/14 16:35
 */
@Data
@Builder
public class MilogAppOpenVo {
    private String label;
    private Long value;
    private String source;
}
