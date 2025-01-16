package run.mone.m78.server.ws;

/**
 * @author wmin
 * @date 2024/7/26
 */
public enum StreamTypeEnum {

    MULTI_MODAL_STATUS("multiModalStatus"),
    MULTI_MODAL_EXECUTE("multiModalExecute");

    private final String type;

    StreamTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

}
