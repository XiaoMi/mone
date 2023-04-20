package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class EsInfoDTO {
    private Long clusterId;
    private String index;

    public EsInfoDTO(Long clusterId, String index) {
        this.clusterId = clusterId;
        this.index = index;
    }
}
