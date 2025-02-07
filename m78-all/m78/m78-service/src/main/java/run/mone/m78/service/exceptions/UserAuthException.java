package run.mone.m78.service.exceptions;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/4/24 16:33
 */
public class UserAuthException extends GenericServiceException {

    private static final String DEFAULT_MESSAGE = "用户认证/鉴权失败! 请查看登录状态.";

    public UserAuthException() {
        super(DEFAULT_MESSAGE, ExCodes.STATUS_FORBIDDEN.getCode());
    }

    public UserAuthException(String message) {
        super(message, ExCodes.STATUS_FORBIDDEN.getCode());
    }

    public UserAuthException(String message, Throwable cause) {
        super(message, cause, ExCodes.STATUS_FORBIDDEN.getCode());
    }

    public UserAuthException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause, ExCodes.STATUS_FORBIDDEN.getCode());
    }
}