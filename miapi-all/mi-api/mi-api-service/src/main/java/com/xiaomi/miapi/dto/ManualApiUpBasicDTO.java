package com.xiaomi.miapi.dto;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class ManualApiUpBasicDTO implements Serializable {
    private Integer apiID;
    private Integer projectID;
    private String opUsername;
    private String updateMsg;
}
