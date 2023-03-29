package com.xiaomi.mone.log.api.enums;

import lombok.Getter;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/9/6 15:05
 */
@Getter
public enum K8sPodTypeEnum {
    /**
     * pod 名称不变即日志路径不变
     */
    STATEFUL,
    STATELESS
}
