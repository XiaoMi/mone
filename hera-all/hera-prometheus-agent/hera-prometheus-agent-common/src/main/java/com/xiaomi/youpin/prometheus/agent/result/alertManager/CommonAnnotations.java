package com.xiaomi.youpin.prometheus.agent.result.alertManager;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonAnnotations {
    private String summary;
    private String title;
}
