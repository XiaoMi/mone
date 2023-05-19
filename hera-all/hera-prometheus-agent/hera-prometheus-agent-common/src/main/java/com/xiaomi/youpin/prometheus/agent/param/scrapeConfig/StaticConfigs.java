package com.xiaomi.youpin.prometheus.agent.param.scrapeConfig;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StaticConfigs {
    private List<String> targets;
    private Map<String, String> labels;
}
