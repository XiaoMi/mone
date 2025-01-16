package run.mone.m78.service.exceptions;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/10/24 10:49 AM
 */
public class InternalException extends GenericServiceException {

    private static final String DEFAULT_MESSAGE = "内部或依赖资源发生未知错误，请联系管理员";

    public InternalException() {

        super(DEFAULT_MESSAGE, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }

    public InternalException(String message) {

        super(message, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }

    public InternalException(String message, Throwable cause) {

        super(message, cause, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }

    public InternalException(Throwable cause) {

        super(DEFAULT_MESSAGE, cause, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }
}
