package run.mone.mimeter.dashboard.bo.operationlog;

public enum OperationTypeEnum {

    VIEW_SNAPSHOT("VIEW_SNAPSHOT", "查看快照"),
    VIEW_REPORT("VIEW_REPORT", "查看报告"),
    DEBUG_RECORD("DEBUG_RECORD", "调试记录"),
    COPY_SNAPSHOT("COPY_SNAPSHOT", "复制快照");

    public String typeName;

    public String typeCname;

    OperationTypeEnum(String typeName, String typeCname) {
        this.typeName = typeName;
        this.typeCname = typeCname;
    }

}
