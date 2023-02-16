package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

@Data
public class RocketMqResponseDTO<T> {
    private Integer code;
    private String message;
    private String requestId;
    private String cost;
    private T data;


    @Data
    public static class SubGroup {
        private String orgId;
        private String name;
    }
}
