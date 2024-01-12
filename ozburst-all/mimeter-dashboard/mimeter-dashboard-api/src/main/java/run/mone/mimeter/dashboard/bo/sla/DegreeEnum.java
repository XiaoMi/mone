package run.mone.mimeter.dashboard.bo.sla;

public enum DegreeEnum {

    //敏感程度
    Sensitive(1, "敏感，连续一次"),
    Tolerable(2, "可容忍，连续三次"),
    NotSensitive(3, "不敏感，连续五次");


    public final int code;

    public String degreeName;
    public final String degreeCname;

    DegreeEnum(int code, String degreeCname) {
        this.code = code;
        this.degreeCname = degreeCname;
    }
}
