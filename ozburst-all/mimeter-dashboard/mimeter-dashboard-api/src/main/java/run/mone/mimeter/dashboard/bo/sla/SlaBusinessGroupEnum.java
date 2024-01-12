package run.mone.mimeter.dashboard.bo.sla;

public enum SlaBusinessGroupEnum {

    InternetService("InternetService", "互联网服务"),
    InternetGame("InternetGame", "互联网游戏");

    public String businessGroupName;
    public String businessGroupCname;

    SlaBusinessGroupEnum(String businessGroupName, String businessGroupCname) {
        this.businessGroupName = businessGroupName;
        this.businessGroupCname = businessGroupCname;
    }
}
