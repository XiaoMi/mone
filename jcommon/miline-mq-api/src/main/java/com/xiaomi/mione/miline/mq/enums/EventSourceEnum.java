package com.xiaomi.mione.miline.mq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MQ 事件来源枚举
 *
 * @author qoder
 */
@Getter
@AllArgsConstructor
public enum EventSourceEnum {

    /**
     * miline平台
     */
    MILINE("miline", "miline平台"),
    APP_STACK("appStack", "appStack平台");

    private final String code;
    private final String desc;

}
