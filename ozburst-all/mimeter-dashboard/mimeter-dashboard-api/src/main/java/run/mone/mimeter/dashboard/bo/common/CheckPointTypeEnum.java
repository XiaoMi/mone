package run.mone.mimeter.dashboard.bo.common;


public enum CheckPointTypeEnum {

    STATUS_CODE(1),
    HEADER_CODE(2),
    OUTPUT_CODE(3);

    public final int code;

    CheckPointTypeEnum(int code) {
        this.code = code;
    }
}
