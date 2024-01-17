package run.mone.mimeter.dashboard.bo.sla;

public enum ActionEnum {

    //报警级别
    WARNING("WARNING", "警告通知"),
    ERROR("ERROR", "停止压测");

    public String degreeName;
    public String degreeCname;

    ActionEnum(String degreeName, String degreeCname) {
        this.degreeName = degreeName;
        this.degreeCname = degreeCname;
    }
}
