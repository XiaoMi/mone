package com.xiaomi.mone.log.manager.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ZoneDTO {

    /**
     * 英文名
     */
    private String zoneNameEN;

    /**
     * 中文名
     */
    private String zoneNameCN;

    /**
     * pod集合
     */
    private List<PodDTO> podDTOList;
}
