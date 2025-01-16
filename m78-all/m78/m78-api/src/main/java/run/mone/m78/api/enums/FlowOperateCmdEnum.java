package run.mone.m78.api.enums;


/**
 * @author wmin
 * @date 2024/2/29
 */
public enum FlowOperateCmdEnum {

    CANCEL_FLOW("cancelFlow"),
    MANUAL_CONFIRM_FLOW("manualConfirmFlow"),
    GOTO_FLOW("gotoFlow"),
    MODIFY_PARAM("modifyParam"),
    NOTIFY_SUB_FLOW_STATUS("notifySubFlowStatus");

    private final String name;

    FlowOperateCmdEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static FlowOperateCmdEnum getEnumByName(String name) {
        for (FlowOperateCmdEnum value : FlowOperateCmdEnum.values()) {
            if (value.getName().equals(name)) {
                return value;
            }
        }
        return null;
    }
}
