package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MilogAgentDTO {
    private Long id;
    private String ip;
    private String container;
    private Integer state;
    private String version;
    private Long updateTime;
    private String computerRoomName;
    private List<AgentLogProcessDTO> processList;
}
