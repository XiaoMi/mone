package run.mone.mimeter.engine.common;

/**
 * @author goodjava@qq.com
 * @date 2022/9/20 22:35
 */
public enum ErrorCode {

    ERROR_505(505, "main_thread_latch_timeout"),

    ERROR_500(500, "500 error");

    public final int code;

    public final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }


}
