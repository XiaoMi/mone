package com.xiaomi.mone.log.stream.job.compensate;

import com.xiaomi.mone.log.model.EsInfo;
import lombok.Data;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/1/17 10:57
 */
@Data
public class MqMessageDTO {

    private EsInfo esInfo;

    private List<CompensateMqDTO> compensateMqDTOS;

    @Data
    public static class CompensateMqDTO {
        private String esIndex;
        private String msg;
    }

}
