package run.mone.hera.operator.common;

public enum ErrorCode {
    success(0, "success"),
    unknownError(1, "unknown error"),

    // 参数问题
    invalidParamError(1001, "无效的参数"),
    DeleteJobFail(1006,"请求接口删除失败"),
    UpdateJobFail(1007,"请求接口更新失败"),
    RequestBodyIsEmpty(1008,"请求体为空"),
    ScrapeIdIsEmpty(1009,"查询的抓取id为空"),
    ThisUserNotHaveAuth(1010,"该用户无此权限"),
    NoOperPermission(1013,"无操作权限"),
    OperFailed(1014,"操作失败"),
    REPEAT_ADD_PROJECT(1016,"重复添加项目"),
    UNKNOWN_TYPE(1017,"未知的类型"),
    INVALID_USER(4001,"用户身份无效"),
    NO_DATA_FOUND(4004,"数据未找到");

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
}
