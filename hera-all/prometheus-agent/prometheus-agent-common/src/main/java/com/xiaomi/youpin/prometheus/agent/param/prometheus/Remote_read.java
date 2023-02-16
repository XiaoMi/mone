package com.xiaomi.youpin.prometheus.agent.param.prometheus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Remote_read {

    private String url;
    private boolean read_recent;
    private String name;
    private boolean enable_http2;

}
