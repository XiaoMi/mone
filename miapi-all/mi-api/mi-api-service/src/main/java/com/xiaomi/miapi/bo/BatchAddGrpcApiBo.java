package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class BatchAddGrpcApiBo implements Serializable {
    private List<GrpcServiceMethod> serviceMethods;
    private String env;
    private String appName;
    private String symbol;
    private String ip;
    private Integer port;
    private Integer projectID;
    private Boolean forceUpdate;
    private String updateUserName;
}
