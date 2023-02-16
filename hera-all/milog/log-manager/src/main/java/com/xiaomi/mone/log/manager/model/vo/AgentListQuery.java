package com.xiaomi.mone.log.manager.model.vo;

import lombok.Data;

@Data
public class AgentListQuery {
    private String ip;
    private String isDeployed;
    private Integer page;
    private Integer pageSize;
}
