package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/13 15:33
 */
@Data
public class RadarResponseDTO<T> {
    private String code;
    private String message;
    private RadarData<T> data;

    @Data
    public static class RadarData<T> {
        private Integer pageSize;
        private Integer total;
        private Integer page;
        private Integer pageIndex;
        private T list;
    }
}
