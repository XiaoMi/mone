package com.xiaomi.youpin.infra.rpc.errors;

/**
 * Created by daxiong on 2018/8/21.
 */
public final class ExceptionHelper {
    private ExceptionHelper() {
    }

    public static BizError create(ErrorCode errorCode, String message) {
        return new BizError(errorCode.getCode(), message);
    }

    public static BizError create(ErrorCode errorCode, String message, boolean writableStackTrace) {
        return new BizError(errorCode.getCode(), message, writableStackTrace);
    }

    public static BizError create(ErrorCode errorCode, String message, Throwable e) {
        return new BizError(errorCode.getCode(), message, e, true);
    }

    public static BizError create(ErrorCode errorCode, String message, Throwable e, boolean writableStackTrace) {
        return new BizError(errorCode.getCode(), message, e, writableStackTrace);
    }
}
