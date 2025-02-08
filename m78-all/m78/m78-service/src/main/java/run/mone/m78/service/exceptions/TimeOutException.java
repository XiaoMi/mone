package run.mone.m78.service.exceptions;

/**
 * @author HawickMason@xiaomi.com
 * @date 4/9/24 16:11
 */
public class TimeOutException extends GenericServiceException {

    private static final String DEFAULT_MESSAGE = "调用超时";

    public TimeOutException() {

        super(DEFAULT_MESSAGE, ExCodes.STATUS_TIME_OUT.getCode());
    }

    public TimeOutException(String message) {

        super(message, ExCodes.STATUS_TIME_OUT.getCode());
    }

    public TimeOutException(String message, Throwable cause) {

        super(message, cause, ExCodes.STATUS_TIME_OUT.getCode());
    }

    public TimeOutException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, ExCodes.STATUS_TIME_OUT.getCode());
    }
}
