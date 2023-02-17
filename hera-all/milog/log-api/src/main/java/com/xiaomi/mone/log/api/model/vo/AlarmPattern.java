package com.xiaomi.mone.log.api.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author: wtt
 * @date: 2022/5/24 14:59
 * @description:
 */
@Data
@Builder
public class AlarmPattern {
    /**
     * 匹配到的次数
     */
    private Integer count;

    private String matchMessage;
}
