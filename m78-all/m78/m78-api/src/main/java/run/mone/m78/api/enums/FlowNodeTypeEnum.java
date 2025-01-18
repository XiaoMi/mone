package run.mone.m78.api.enums;


/**
 * @author wmin
 * @date 2024/2/29
 */
public enum FlowNodeTypeEnum {

    BEGIN("begin"),
    END("end"),
    LLM("llm"),
    KNOWLEDGE("knowledge"),
    CODE("code"),
    PLUGIN("plugin"),
    PRECONDITION("precondition"),
    NEW_PRECONDITION("newPrecondition"),
    MANUAL_CONFIRM("manualConfirm"),
    LLM_IMAGE_UNDERSTAND("llmImageUnderstand"),
    LLM_FILE_UNDERSTAND("llmFileUnderstand"),
    INTENT_RECOGNITION("intentRecognition"),
    SUB_FLOW("subFlow"),
    WORK_CHART("workChart"),
    STYLE_REPAINT("styleRepaint"),
    DATABASE("database");

    private final String desc;

    FlowNodeTypeEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static FlowNodeTypeEnum getEnumByDesc(String desc) {
        for (FlowNodeTypeEnum value : FlowNodeTypeEnum.values()) {
            if (value.getDesc().equals(desc)) {
                return value;
            }
        }
        return null;
    }
}
