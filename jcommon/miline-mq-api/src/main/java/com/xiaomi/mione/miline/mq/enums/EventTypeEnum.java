package com.xiaomi.mione.miline.mq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务层的消息类型。 在tag下无法区分的业务消息类型, 比tag更细粒度。
 *
 * @author qoder
 */
@Getter
@AllArgsConstructor
public enum EventTypeEnum {

    ;

    private final String type;
    private final String desc;
}
