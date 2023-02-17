package com.xiaomi.mone.log.manager.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailLogProcessDTO {
    private String tailName;
    private String ip;
    private String path;
    private Long fileRowNumber;
    private Long collectTime;
    private String collectPercentage;

}
