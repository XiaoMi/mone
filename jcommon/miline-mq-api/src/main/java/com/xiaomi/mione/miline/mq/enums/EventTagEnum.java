package com.xiaomi.mione.miline.mq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MQ 事件的tag， 用于区分一类的事件消息， 比如： 订单创建消息、 订单支付消息、 订单取消消息等
 *
 * @author qoder
 */
@Getter
@AllArgsConstructor
public enum EventTagEnum {
    /**
     * 创建应用变更
     */
    EVENT_CREATE_APP_CHANGE("createAppChange", "启动开发事件"),
    /**
     * 申请应用部署(去部署)
     */
    EVENT_CREATE_DEPLOY_REQUEST("createDeployRequest", "去部署"),
    /**
     * 开始部署
     */
    EVENT_DEPLOY_START("deployStart", "开始部署"),
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
