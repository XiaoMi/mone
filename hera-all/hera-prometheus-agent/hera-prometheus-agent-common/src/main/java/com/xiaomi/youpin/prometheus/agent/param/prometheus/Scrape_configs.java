package com.xiaomi.youpin.prometheus.agent.param.prometheus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Scrape_configs {

    private String job_name;
    private boolean honor_labels;
    private String metrics_path;
    private List<File_sd_configs> file_sd_configs;
    private List<Static_configs> static_configs;
    private List<Relabel_configs> relabel_configs;
    private List<Http_sd_configs> http_sd_configs;
    private List<Metric_relabel_configs> metric_relabel_configs;
    private Authorization authorization;
    private Tls_config tls_config;
    private Map<String, List<String>> params;

}
