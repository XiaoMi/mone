package com.xiaomi.mone.monitor.result;


import org.apache.commons.lang3.StringUtils;

/**
 * @author gaoxihui
 * @date 2021/7/10 7:36 下午
 */
public enum ErrorCode {
    success(0, "success"),
    unknownError(1, "unknown error"),
    CREATE_ALERT_FAILURE(2, "failed to create alert"),
    SUBMIT_FLINK_JOB(3, "failed to submit flink job"),
    ALERT_NOT_FOUND(4, "Alert not found"),
    ALERT_REMOVE_FAILED(5, "failed to remove alert"),

    // 参数问题
    invalidParamError(1001, "无效的参数"),
    nonExistentScrapeId(1002,"不存在的抓取id"),
    CannotDeleteADeletedJob(1003,"不能删除一个已经删除的job"),
    CannotUpdateANonExistingJob(1004,"不能更新一个不存在的job"),
    OnlyJobsThatHaveBeenCreatedSuccessfullyCanBeUpdated(1005,"只能更新创建成功的job"),
    DeleteJobFail(1006,"请求接口删除失败"),
    UpdateJobFail(1007,"请求接口更新失败"),
    RequestBodyIsEmpty(1008,"请求体为空"),
    ScrapeIdIsEmpty(1009,"查询的抓取id为空"),
    ThisUserNotHaveAuth(1010,"该用户无此权限"),
    nonExistentServiceMarketId(1011,"不存在的服务大盘id"),
    AlertGroupNoExist(1012,"告警组不存在"),
    NoOperPermission(1013,"无操作权限"),
    OperFailed(1014,"操作失败"),
    nonExistentStrategy(1015,"规则策略不存在"),
    REPEAT_ADD_PROJECT(1016,"重复添加项目"),
    UNKNOWN_TYPE(1017,"未知的类型"),
    ALERT_GROUP_USED_FAIL(1018,"使用中不能删除"),
    ALARM_STRATEGY_INFO_UPDATE_FAIL(1019,"策略信息更新数据库失败"),
    nonExistentAlarmRule(1020,"报警规则不存在"),
    ALARM_RULE_INFO_UPDATE_FAIL(1021,"报警规则信息更新数据库失败"),
    ALERT_TEAM_AND_ALERT_MEMBERS_BOTH_EMPTY(1022,"报警组和报警通知人不可同时为空"),
    FAIL_TO_DELETE_RULE_IN_DB(1023,"删除报警规则数据失败"),

    INVALID_USER(4001,"用户身份无效"),
    NO_DATA_FOUND(4004,"数据未找到"),

    API_KEY_CREATE_FAIL(5001,"Grafana api key创建失败，请检查传入的Grafana用户名及密码"),
    DATASOURCE_CREATE_FAIL(5002,"Grafana datasource创建失败"),
    FOLDER_CREATE_FAIL(5003,"Grafana folder创建失败");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }


    public String getConvertMsg(String realMsg) {
        if (StringUtils.isBlank(realMsg)) {
            return message;
        }
        if (realMsg.contains("chat_id not exist")) {
            return "飞书ID不存在";
        }
        if (realMsg.contains("is used by alert")) {
            return "告警组已被使用，不能删除";
        }
        if (realMsg.contains("bot not in chat")) {
            return "请把Falcon报警或Falcon报警演练(staging)拉入飞书群";
        }
        return realMsg;
    }
}
