package com.xiaomi.mone.app.model.dto;

import lombok.Data;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/10/13 15:33
 */
@Data
public class MisResponseDTO<T> {
    private String code;
    private T data;
}
