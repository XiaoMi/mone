package run.mone.m78.service.exceptions;


/**
 * @author HawickMason@xiaomi.com
 * @date 1/10/24 10:50 AM
 */
public class InvalidArgumentException extends GenericServiceException {

    private static final String DEFAULT_MESSAGE = "请求的参数不正确。";

    public InvalidArgumentException() {

        super(DEFAULT_MESSAGE, ExCodes.STATUS_BAD_REQUEST.getCode());
    }

    public InvalidArgumentException(String message) {

        super(message, ExCodes.STATUS_BAD_REQUEST.getCode());
    }

    public InvalidArgumentException(String message, Throwable cause) {

        super(message, cause, ExCodes.STATUS_BAD_REQUEST.getCode());
    }

    public InvalidArgumentException(Throwable cause) {

        super(DEFAULT_MESSAGE, cause, ExCodes.STATUS_BAD_REQUEST.getCode());
    }

}

