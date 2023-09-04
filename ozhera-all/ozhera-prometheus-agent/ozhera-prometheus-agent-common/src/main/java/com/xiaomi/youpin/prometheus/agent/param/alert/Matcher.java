package com.xiaomi.youpin.prometheus.agent.param.alert;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class Matcher {
    private String name;  //label name
    private String value;  //匹配的值，可以是 label value 或则正则表达式
    private boolean isRegex;  //是否是正则匹配
    private boolean isEqual;  //是否是等于匹配
}
