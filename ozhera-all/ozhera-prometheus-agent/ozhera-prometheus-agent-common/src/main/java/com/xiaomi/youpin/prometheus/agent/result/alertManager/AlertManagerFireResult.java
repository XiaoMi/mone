package com.xiaomi.youpin.prometheus.agent.result.alertManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlertManagerFireResult {

    private String receiver;
    private String status;
    private List<Alerts> alerts;
    private GroupLabels groupLabels;
    private CommonLabels commonLabels;
    private CommonAnnotations commonAnnotations;
    private String externalURL;
    private String version;
    private String groupKey;
    private int truncatedAlerts;
}
