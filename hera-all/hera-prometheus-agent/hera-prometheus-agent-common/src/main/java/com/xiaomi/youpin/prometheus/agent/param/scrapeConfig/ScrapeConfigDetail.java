package com.xiaomi.youpin.prometheus.agent.param.scrapeConfig;

import com.xiaomi.youpin.prometheus.agent.param.prometheus.*;
import lombok.Data;
import lombok.ToString;
import java.util.Map;
import java.util.List;

@Data
@ToString(callSuper = true)
public class ScrapeConfigDetail {

    private String job_name;
    private String scrape_interval;
    private String scrape_timeout;
    private String metrics_path;
    private boolean honor_labels;
    private boolean honor_timestamps;
    private String scheme;
    private Map<String, List<String>> params;
    private BasicAuth basic_auth;
    private List<Static_configs> static_configs;
    private List<Relabel_configs> relabel_configs;
    private List<Http_sd_configs> http_sd_configs;
    private List<Metric_relabel_configs> metric_relabel_configs;

}
