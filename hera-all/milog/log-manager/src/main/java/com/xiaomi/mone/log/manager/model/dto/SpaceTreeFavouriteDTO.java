package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SpaceTreeFavouriteDTO {
    private String label;
    private Long value;
    private List<StoreTreeDTO> children;
}
