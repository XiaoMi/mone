package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class GrpcServiceMethod implements Serializable {
    private Integer groupId;
    private String serviceName;
    private List<String> methodNames;
}
