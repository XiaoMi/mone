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
     * 创建应用变更
     */
    EVENT_CREATE_APP_CHANGE("createAppChange", "启动开发事件"),
    /**
     * 申请应用部署(去部署)
     */
    EVENT_CREATE_DEPLOY_REQUEST("createDeployRequest", "去部署"),

    /**
     * 完成部署
     */
    EVENT_DEPLOY_COMPLETE("deployComplete", "完成部署"),

    /**
     * 取消部署
     */
    EVENT_CANCEL_DEPLOY("cancelDeploy", "取消部署"),

    /**
     * 回滚部署
     */
    EVENT_ROLLBACK_DEPLOY("rollbackDeploy", "回滚部署"),

    /**
     * 应用变更状态变更消息
     */
    EVENT_APP_CHANGE_STATUS("appChangeStatus", "应用变更状态变更消息");

    private final String tag;
    private final String desc;
}
