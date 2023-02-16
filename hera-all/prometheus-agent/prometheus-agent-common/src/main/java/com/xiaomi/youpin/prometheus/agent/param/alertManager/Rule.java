package com.xiaomi.youpin.prometheus.agent.param.alertManager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Rule {
    private String alert;
    private String Expr;
    private String For;
    private Map<String,String> labels;
    private Map<String,String> annotations;

}
