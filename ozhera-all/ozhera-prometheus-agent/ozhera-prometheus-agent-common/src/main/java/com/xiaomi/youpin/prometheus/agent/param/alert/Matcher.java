package com.xiaomi.youpin.prometheus.agent.param.alert;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Matcher {
    private String name;  //label name
    private String value;  //Matching values can be label values or regular expressions.
    private boolean isRegex;  //Whether it is a regular expression match
    private boolean isEqual;  //Whether it is an equal match
}
