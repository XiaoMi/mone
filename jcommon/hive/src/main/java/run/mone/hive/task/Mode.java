package run.mone.hive.task;

/**
 * 任务执行模式枚举
 * 对应Cline中的Mode类型
 */
public enum Mode {
    /**
     * 计划模式 - 用于分析和规划任务
     */
    PLAN("plan"),
    
    /**
     * 执行模式 - 用于实际执行任务
     */
    ACT("act");
    
    private final String value;
    
    Mode(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    /**
     * 从字符串值获取Mode枚举
     */
    public static Mode fromString(String value) {
        for (Mode mode : Mode.values()) {
            if (mode.value.equalsIgnoreCase(value)) {
                return mode;
            }
        }
        throw new IllegalArgumentException("Unknown mode: " + value);
    }
}
