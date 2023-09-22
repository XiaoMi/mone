package com.xiaomi.youpin.prometheus.agent.param.alert;

import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
public class AMSilenceResponse {
    private String silenceID;
}
