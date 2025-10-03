package run.mone.hive.task;

/**
 * Init命令执行异常
 */
public class InitExecutionException extends RuntimeException {
    
    public InitExecutionException(String message) {
        super(message);
    }
    
    public InitExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}