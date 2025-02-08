package run.mone.m78.service.exceptions;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/2/24 2:15 PM
 */
public class QueryException extends GenericServiceException {

    private static final String DEFAULT_MESSAGE = "查询执行错误";

    public QueryException() {

        super(DEFAULT_MESSAGE, ExCodes.STATUS_INNER_QUERY_ERROR.getCode());
    }

    public QueryException(String message) {

        super(message, ExCodes.STATUS_INNER_QUERY_ERROR.getCode());
    }

    public QueryException(String message, Throwable cause) {

        super(message, cause, ExCodes.STATUS_INNER_QUERY_ERROR.getCode());
    }

    public QueryException(Throwable cause) {

        super(DEFAULT_MESSAGE, cause, ExCodes.STATUS_INNER_QUERY_ERROR.getCode());
    }
}
