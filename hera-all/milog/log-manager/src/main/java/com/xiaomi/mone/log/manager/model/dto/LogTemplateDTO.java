package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class LogTemplateDTO {
    private Long value;
    private String label;
    private Integer type;
    private String describe;

    private LogTemplateDetailDTO logTemplateDetailDTOList;
}
