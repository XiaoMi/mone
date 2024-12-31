package run.mone.knowledge.service.exceptions;

import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
import com.xiaomi.youpin.infra.rpc.errors.ErrorScope;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/11/24 11:20
 */
public class ExCodes {


    public static final ErrorScope DEFAULT_ERROR_SCOPE = ErrorScope.createOnce(0);
    public static final ErrorCode STATUS_FORBIDDEN = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 403);

    public static final ErrorCode STATUS_CONFLICT = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 409);

    public static final ErrorCode STATUS_TOO_MANY_REQUESTS = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 429);

    public static final ErrorCode STATUS_INTERNAL_ERROR = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 500);

    public static final ErrorCode STATUS_INNER_QUERY_ERROR = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 600);

    public static final ErrorCode STATUS_BAD_REQUEST = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 400);

    public static final ErrorCode STATUS_METHOD_NOT_ALLOWED = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 405);

    public static final ErrorCode STATUS_NOT_FOUND = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 404);

    public static final ErrorCode STATUS_INVALID_ARGUMENT = ErrorCode.createOnce(DEFAULT_ERROR_SCOPE, 408);
}
