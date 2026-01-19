package com.xiaomi.mione.miline.mq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MQ 节点操作事件类型枚举
 *
 * @author qoder
 */
@Getter
@AllArgsConstructor
public enum OperateNodeEventTypeEnum {

    /**
     * 启动开发
     */
    EVENT_START_CODING(1, "启动开发事件"),
    /**
     * 启动部署
     */
    EVENT_START_DEPLOY(6, "去部署"),

    /**
     * 完成部署
     */
    EVENT_DEPLOY_COMPLETE(7, "完成部署"),

    /**
     * 取消部署
     */
    EVENT_CANCEL_DEPLOY(8, "取消部署"),

    /**
     * 回滚部署
     */
    EVENT_ROLLBACK_DEPLOY(9, "回滚部署"),

    /**
     * 应用变更状态变更消息
     */
    EVENT_APP_CHANGE_STATUS(10, "应用变更状态变更消息");

    private final Integer code;
    private final String desc;
}
