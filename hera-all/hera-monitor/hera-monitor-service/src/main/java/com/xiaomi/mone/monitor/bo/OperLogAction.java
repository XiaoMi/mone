package com.xiaomi.mone.monitor.bo;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/14 14:31
 */
public enum OperLogAction {
    STRATEGY_START("STRATEGY_START", "策略启动"),
    STRATEGY_STOP("STRATEGY_STOP", "策略停止"),
    STRATEGY_DELETE("STRATEGY_DELETE", "策略删除"),
    STRATEGY_EDIT("STRATEGY_EDIT", "策略编辑"),
    RULE_DELETE("RULE_DELETE", "报警规则删除"),
    RULE_EDIT("RULE_EDIT", "报警规则编辑"),
    STRATEGY_ADD("STRATEGY_ADD", "策略添加"),
    ALERT_GROUP_ADD("ALERT_GROUP_ADD", "通知组创建"),
    ALERT_GROUP_EDIT("ALERT_GROUP_EDIT", "通知组编辑"),
    ;
    private String action;
    private String desc;

    OperLogAction(String action, String desc){
        this.action = action;
        this.desc = desc;
    }

    public String getAction() {
        return action;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "OperLogAction{" +
                "action='" + action + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
