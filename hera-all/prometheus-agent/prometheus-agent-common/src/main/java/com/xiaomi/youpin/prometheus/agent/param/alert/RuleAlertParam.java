package com.xiaomi.youpin.prometheus.agent.param.alert;
import com.xiaomi.youpin.prometheus.agent.param.BaseParam;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString(callSuper = true)
public class RuleAlertParam extends BaseParam {
    private String alert;
    private String cname;
    private String expr;
    private String For;
    private Map<String,String> labels;
    private Map<String,String> annotations;
    private String group;
    private String priority;
    private List<String> env;
    private List<String> alert_member;
    private Integer enabled;
    private String createdBy;
    private String promCluster;
    private List<String> alert_at_people;

    public boolean argCheck() {
        return true;
    }
}
