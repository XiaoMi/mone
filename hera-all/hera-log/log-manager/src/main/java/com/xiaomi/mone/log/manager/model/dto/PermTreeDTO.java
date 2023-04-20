package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PermTreeDTO {
    private String id;
    private String label;
    public Integer deptLevel;
    private List<PermTreeDTO> children;
}
