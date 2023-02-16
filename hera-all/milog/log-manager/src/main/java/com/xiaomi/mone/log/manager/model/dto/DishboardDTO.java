package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class DishboardDTO {
    private String dishboardName;
    private List<DashboardGraphDTO> graphList;
}
