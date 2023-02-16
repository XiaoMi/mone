package com.xiaomi.mone.log.api.model.meta;

import lombok.Data;

import java.util.List;

@Data
public class AgentDefine {
    private List<FilterConf> filters;
}
