package com.xiaomi.mone.log.api.model.meta;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class AgentDefine implements Serializable {
    private List<FilterConf> filters;
}
