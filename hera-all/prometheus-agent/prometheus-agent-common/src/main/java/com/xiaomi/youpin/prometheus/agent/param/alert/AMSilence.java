package com.xiaomi.youpin.prometheus.agent.param.alert;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(callSuper = true)
public class AMSilence {
    private Long startsAt;
    private Long endsAt;
    private String comment;
    private List<Matcher> matchers;
    private String createdBy;
    private String id;
    private AMSilenceStatus status;
}
