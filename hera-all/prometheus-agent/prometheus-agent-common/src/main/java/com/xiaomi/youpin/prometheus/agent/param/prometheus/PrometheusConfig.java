package com.xiaomi.youpin.prometheus.agent.param.prometheus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PrometheusConfig {

    private Global global;
    private List<String> rule_files;
    private List<Remote_write> remote_write;
    private List<Remote_read> remote_read;
    private List<Scrape_configs> scrape_configs;
    private Alerting alerting;
}