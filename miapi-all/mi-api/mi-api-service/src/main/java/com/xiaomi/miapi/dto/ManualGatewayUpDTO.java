package com.xiaomi.miapi.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ManualGatewayUpDTO extends ManualApiUpBasicDTO implements Serializable {
    private String env;
}
