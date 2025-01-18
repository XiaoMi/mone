package run.mone.m78.service.exceptions;

/**
 * @author HawickMason@xiaomi.com
 * @date 4/2/24 14:50
 */
public class NotFoundException extends GenericServiceException {

    private static final String DEFAULT_MESSAGE = "未找到对应资源";

    public NotFoundException() {

        super(DEFAULT_MESSAGE, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }

    public NotFoundException(String message) {

        super(message, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }

    public NotFoundException(String message, Throwable cause) {

        super(message, cause, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }

    public NotFoundException(Throwable cause) {

        super(DEFAULT_MESSAGE, cause, ExCodes.STATUS_INTERNAL_ERROR.getCode());
    }
}