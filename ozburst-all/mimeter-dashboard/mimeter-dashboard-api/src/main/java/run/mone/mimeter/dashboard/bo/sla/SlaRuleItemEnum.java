package run.mone.mimeter.dashboard.bo.sla;

public enum SlaRuleItemEnum {

    //业务指标
    SuccessRate("SuccessRate", "成功率", "%"),
    P99ResponseTime("P99ResponseTime", "P99_RT", "ms"),

    AvgResponseTime("AvgResponseTime", "Avg_RT", "ms"),
    RequestPerSecond("RequestPerSecond", "RPS", "/s"),

    //监控指标
    CpuUtilization("CpuUtilization", "CPU利用率", "%"),
    MemoryUtilization("MemoryUtilization", "内存利用率", "%"),
    Load5Average("Load5Average", "load5平均值", ""),
    Load5Max("Load5Max", "load5最大值", ""),
    DropConnectionAverage("DropConnectionAverage", "丢弃连接数平均值", "个"),
    DropConnectionMax("DropConnectionMax", "丢弃连接数最大值", "个");



    public String ruleItemName;

    public String ruleItemCname;

    public String unit;

    SlaRuleItemEnum(String ruleItemName, String ruleItemCname, String unit) {
        this.ruleItemName = ruleItemName;
        this.ruleItemCname = ruleItemCname;
        this.unit = unit;
    }
}
