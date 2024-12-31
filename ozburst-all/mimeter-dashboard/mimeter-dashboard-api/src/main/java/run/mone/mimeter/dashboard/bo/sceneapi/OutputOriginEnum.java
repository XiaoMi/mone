package run.mone.mimeter.dashboard.bo.sceneapi;

public enum OutputOriginEnum {
    BODY_TXT(1),
    BODY_JSON(2);

    public final int code;

    OutputOriginEnum(int code) {
        this.code = code;
    }
}
