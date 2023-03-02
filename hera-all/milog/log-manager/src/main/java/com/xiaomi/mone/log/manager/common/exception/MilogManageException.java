package com.xiaomi.mone.log.manager.common.exception;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/22 14:46
 */
public class MilogManageException extends RuntimeException {

    public MilogManageException(String message, Throwable cause) {
        super(message, cause);
    }

    public MilogManageException(Throwable cause) {
        super(cause);
    }

    public MilogManageException(String message) {
        super(message);
    }
}
