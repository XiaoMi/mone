package run.mone.mimeter.dashboard.bo.sla;

public enum SlaRuleItemTypeEnum {

    BusinessMetrics("BusinessMetrics", "业务指标"),
    MonitorMetrics("MonitorMetrics", "监控指标");

    public String ruleItemTypeName;
    public String ruleItemTypeCname;

    SlaRuleItemTypeEnum(String ruleItemTypeName, String ruleItemTypeCname) {
        this.ruleItemTypeName = ruleItemTypeName;
        this.ruleItemTypeCname = ruleItemTypeCname;
    }
}
