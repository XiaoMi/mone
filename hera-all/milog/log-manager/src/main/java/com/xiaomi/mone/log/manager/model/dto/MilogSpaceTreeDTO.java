package com.xiaomi.mone.log.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MilogSpaceTreeDTO {
    private String label;
    private Long value;
    private List<MapDTO<String, Long>> children;
}
