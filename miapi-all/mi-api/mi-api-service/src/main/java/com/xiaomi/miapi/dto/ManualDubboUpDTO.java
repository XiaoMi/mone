package com.xiaomi.miapi.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ManualDubboUpDTO extends ManualApiUpBasicDTO implements Serializable {
    private String serviceName;
    private String methodName;
    private String group;
    private String version;
    private String env;
}
