package com.xiaomi.youpin.prometheus.agent.param.prometheus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class Alerting {

    private List<Alertmanagers> alertmanagers;

}
