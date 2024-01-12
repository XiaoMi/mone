package run.mone.mimeter.dashboard.bo.task;

public enum BenchModeEnum {
    //压力模式
    RPS(0),
    CONCURRENT(1);

    public final int code;

    BenchModeEnum(int code) {
        this.code = code;
    }
}
