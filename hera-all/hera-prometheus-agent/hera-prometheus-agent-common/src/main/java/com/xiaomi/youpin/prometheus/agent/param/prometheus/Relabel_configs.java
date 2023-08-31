package com.xiaomi.youpin.prometheus.agent.param.prometheus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Relabel_configs implements Serializable {

    private List<String> source_labels;
    private String regex;
    private String target_label;
    private String replacement;

    private String action;
    private String separator;

}
