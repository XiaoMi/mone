package com.xiaomi.mone.tpc.common.vo;

import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/4 10:34
 */
@ToString
public enum ResponseCode {

    SUCCESS(0, "成功"),
    UNKNOWN_ERROR(99, "未知错误"),
    ARG_ERROR(101, "参数错误"),
    USER_DISABLED(102, "您已被禁用"),
    NO_SUPPORT_LOGIN_MODE(103, "登入模式不支持"),
    NO_OPER_PERMISSION(104, "没有操作权限"),
    OPER_FAIL(105, "操作失败"),
    OPER_ILLEGAL(106, "非法操作"),
    NO_USER_ACCOUNT(107, "请求头没有user"),
    OUTER_CALL_FAILED(108, "外部请求失败"),
    CHECK_FAILED(109, "检查失败"),
    ;

    private int code;
    private String message;
    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public <T> ResultVo<T> build(T data) {
        return build(null, data);
    }

    public <T> ResultVo<T> build(String desc) {
        return build(desc, null);
    }

    public <T> ResultVo<T> build() {
        return build(null, null);
    }
    public <T> ResultVo<T> build(String desc, T data) {
        ResultVo<T> result = new ResultVo<T>();
        result.setData(data);
        result.setCode(code);
        if (StringUtils.isNotBlank(desc)) {
            result.setMessage(desc);
        } else {
            result.setMessage(message);
        }
    return result;
    }

}
