package com.xiaomi.mone.log.manager.model.bo.alert;

import lombok.Data;

/**
 * @author: wtt
 * @date: 2022/5/24 15:03
 * @description:
 */
@Data
public class AlertMsgPattern {
    /**
     * 规则
     */
    private String rule;
    /**
     * 测试字符串
     */
    private String message;
}
