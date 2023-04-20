package com.xiaomi.mone.log.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreTreeDTO {
    private Long value;
    private String label;
    private Integer isFavourite;

    public static StoreTreeDTO Of (Long value, String label, Integer isFavourite) {
        return new StoreTreeDTO(value, label, isFavourite);
    }
}
