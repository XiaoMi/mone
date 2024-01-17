package run.mone.mimeter.dashboard.bo.common;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/30
 */
public enum EmitterTypeEnum {

    FINISH("finish"),

    SLA_WARN("sla_warn"),
    SLA_ERROR("sla_error"),

    TOTAL_STAT_ANALYSIS("total_stat_analysis")
    ;

    private String value;

    EmitterTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
