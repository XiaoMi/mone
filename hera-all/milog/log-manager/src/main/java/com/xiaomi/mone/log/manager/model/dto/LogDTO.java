package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class LogDTO implements Serializable {
    private List<LogDataDTO> logDataDTOList;
    private Long total;
    private Object[] thisSortValue;
}
