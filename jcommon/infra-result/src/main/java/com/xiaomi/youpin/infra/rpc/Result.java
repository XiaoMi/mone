package com.xiaomi.youpin.infra.rpc;

import com.xiaomi.youpin.infra.rpc.errors.BizError;
import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author daxiong
 * @date 2018/12/20
 * @modify goodjava@qq.com
 */
public class Result<T> implements Serializable {

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String message;

    @Getter
    @Setter
    private T data;

    @Getter
    @Setter
    private String traceId;

    /**
     * 附加字段,方便以后扩展
     */
    @Getter
    @Setter
    private Map<String, String> attachments;

    private static String SUCCESS_MESSAGE = "ok";

    /**
     * 不允许自己创建Result，只可以使用静态方法创建
     */
    Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result() {

    }

    public static <T> Result<T> success(T data) {
        return new Result<>(GeneralCodes.OK.getCode(), SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> fail(ErrorCode code, String message) {
        return new Result<>(code.getCode(), message, null);
    }

    public static <T> Result<T> fromException(Throwable e) {
        if (e instanceof BizError) {
            return new Result<>(((BizError) e).getCode(), ((BizError) e).getMessage(), null);
        } else {
            return fail(GeneralCodes.InternalError, "系统繁忙，请稍后再试");
        }
    }

    public static <T> Result<T> fromException(Throwable e, String message) {
        if (e instanceof BizError) {
            return new Result<>(((BizError) e).getCode(), message, null);
        } else {
            return fail(GeneralCodes.InternalError, message);
        }
    }

    @Override
    public String toString() {
        return String.format("Result{code=%d, message=%s, data=%s}", code, message, data);
    }

}
