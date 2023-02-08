package com.xiaomi.youpin.infra.rpc.errors;

import lombok.Getter;

/**
 * Created by daxiong on 2018/12/20.
 */
public class BizError extends Exception {
    /**
     * 构造方法，不记录堆栈信息
     * @param code 错误码，通用错误码参考{@link GeneralCodes}
     * @param msg 异常描述，建议使用英文，以避免日志流转过程中出现编码问题
     */
    BizError(int code, String msg) {
        super(msg, null, true, false);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造方法，可以指定是否记录堆栈
     * @param code 错误码，通用错误码参考{@link GeneralCodes}
     * @param msg 异常描述，建议使用英文，以避免日志流转过程中出现编码问题
     * @param writableStackTrace 指定是否记录堆栈信息，FALSE：不记录，TRUE：记录
     */
    BizError(int code, String msg, boolean writableStackTrace) {
        super(msg, null, true, writableStackTrace);
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构造方法，可以指定是否记录堆栈
     * @param code 错误码，通用错误码参考{@link GeneralCodes}
     * @param msg 异常描述，建议使用英文，以避免日志流转过程中出现编码问题
     * @param cause 触发异常
     * @param writableStackTrace 指定是否记录堆栈信息，FALSE：不记录，TRUE：记录
     */
    BizError(int code, String msg, Throwable cause, boolean writableStackTrace) {
        super(msg, cause, true, writableStackTrace);
        this.code = code;
        this.msg = msg;
    }

    @Getter
    private int code;
    @Getter
    private String msg;

    @Override
    public String toString() {
        return String.format("BizError{code=%d, message=%s}", code, msg);
    }
}
