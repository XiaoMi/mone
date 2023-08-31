package com.xiaomi.youpin.prometheus.agent.param.alert;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class AMSilenceStatus {
    private String state;
}
