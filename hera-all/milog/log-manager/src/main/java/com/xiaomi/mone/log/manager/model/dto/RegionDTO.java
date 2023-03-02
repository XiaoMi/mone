package com.xiaomi.mone.log.manager.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegionDTO {

    /**
     * 英文名
     */
    private String regionNameEN;

    /**
     * 中文名
     */
    private String regionNameCN;

    /**
     * zone集合
     */
    private List<ZoneDTO> zoneDTOList;
}
