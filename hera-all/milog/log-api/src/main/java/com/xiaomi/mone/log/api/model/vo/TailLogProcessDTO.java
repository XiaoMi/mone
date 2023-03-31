package com.xiaomi.mone.log.api.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TailLogProcessDTO implements Serializable {
    private String tailName;
    private String ip;
    private String path;
    private Long fileRowNumber;
    private Long collectTime;
    private String collectPercentage;

}
