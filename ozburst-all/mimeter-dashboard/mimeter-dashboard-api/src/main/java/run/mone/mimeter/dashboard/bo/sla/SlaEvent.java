package run.mone.mimeter.dashboard.bo.sla;

import lombok.Data;

import java.io.Serializable;

@Data
public class SlaEvent extends BenchEvent implements Serializable {
    //若业务指标触发则有以下几项
    private int triggerApiId;
    private String apiName;
    private String apiUrlOrServiceName;

    private String alarmLevel;
    private int degree;
    private String slaRuleName;
    private String ruleItemType;
    private String ruleItem;
    private String condition;
    private String triggerItem;
    private Double value;
    private Double ruleTargetValue;
}
