package run.mone.mimeter.dashboard.bo.task;

public enum BenchIncreaseModeEnum {

    //Rps压力增加模式 0 固定（） 1 手动 2 百分比递增
    STABLE(0),
    MANUAL(1),
    PERCENT_INCREASE(2);

    public final int code;

    BenchIncreaseModeEnum(int code) {
        this.code = code;
    }
}
