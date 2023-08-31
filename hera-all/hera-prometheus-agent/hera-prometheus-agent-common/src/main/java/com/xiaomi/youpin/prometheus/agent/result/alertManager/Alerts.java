package com.xiaomi.youpin.prometheus.agent.result.alertManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Alerts {
    private String status;
    private Labels labels;
    private Annotations annotations;
    private Date startsAt;
    private Date endsAt;
    private String generatorURL;
    private String fingerprint;
}
