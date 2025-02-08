package run.mone.m78.service.exceptions;

import lombok.Getter;

/**
 * 通用错误父类
 * @author HawickMason@xiaomi.com
 * @date 1/10/24 10:40 AM
 */
@Getter
public class GenericServiceException extends RuntimeException {

    private int code;

    public GenericServiceException(String message) {
        super(message);
        this.code = -1;
    }

    public GenericServiceException(String message, int code) {
        super(message);
        this.code = code;
    }

    public GenericServiceException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }
}


