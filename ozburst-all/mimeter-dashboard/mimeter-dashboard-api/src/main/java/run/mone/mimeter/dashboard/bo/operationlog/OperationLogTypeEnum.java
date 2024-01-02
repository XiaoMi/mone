package run.mone.mimeter.dashboard.bo.operationlog;

public enum OperationLogTypeEnum {

    NEW_OPERATION(1, "NEW"),
    UPDATE_OPERATION(2, "UPDATE"),
    DELETE_OPERATION(3, "DELETE"),
    START_BENCH(4, "START_BENCH"),
    STOP_BENCH(5, "STOP_BENCH"),
    DEBUG_OPERATION(5, "DEBUG");

    public int typeCode;
    public String typeName;

    OperationLogTypeEnum(int typeCode, String typeName) {
        this.typeCode = typeCode;
        this.typeName = typeName;
    }

}
