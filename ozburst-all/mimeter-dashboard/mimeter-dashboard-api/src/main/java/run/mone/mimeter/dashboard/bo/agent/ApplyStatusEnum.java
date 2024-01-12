package run.mone.mimeter.dashboard.bo.agent;

public enum ApplyStatusEnum {

    UnAuditing(0, "UnAuditing"),
    ApplyPass(1, "ApplyPass"),

    ApplyRefuse(2, "ApplyRefuse"),

    ;



    public final int statusCode;
    public final String statusName;

    ApplyStatusEnum(int statusCode, String statusName) {
        this.statusCode = statusCode;
        this.statusName = statusName;
    }

}
