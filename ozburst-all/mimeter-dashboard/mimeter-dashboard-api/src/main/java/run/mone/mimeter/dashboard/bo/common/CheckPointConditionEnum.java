package run.mone.mimeter.dashboard.bo.common;


public enum CheckPointConditionEnum {

    BIGGER(1),
    BIGGER_AND_EQ(2),
    SMLLER(3),
    SMALLER_AND_EQ(4),
    EQ(5),
    CONTAIN(6),
    NOT_CONTAIN(7),
    NOT_EQ(8),
    ;

    public final int code;

    CheckPointConditionEnum(int code) {
        this.code = code;
    }
}
