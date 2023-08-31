package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlertManagerRule implements Serializable {

    private String rule_name;
    private String rule_fn;
    private int rule_interval;
    private String rule_alert;
    private String rule_expr;
    private String rule_for;
    private String rule_labels;
    private String rule_annotations;
    private String principal;

    public String toString() {
        return rule_name + " " + rule_fn + " " + rule_interval + " " + rule_alert + " " + rule_expr + " " + rule_for + " " + rule_labels + " " + rule_annotations + " " + principal;
    }
}
