package run.mone.mimeter.dashboard.common;

public enum TaskStatus {
    Init(0),
    Success(1),
    Failure(2),
    Retry(3),
    Running(4),
    STOPPED(5),

    ;

    public int code;

    private TaskStatus(int code) {
        this.code = code;
    }
}