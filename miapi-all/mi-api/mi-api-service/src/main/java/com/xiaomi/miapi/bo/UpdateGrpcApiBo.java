package com.xiaomi.miapi.bo;

import lombok.Data;

import java.io.Serializable;
/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Data
public class UpdateGrpcApiBo implements Serializable {
    private Integer projectId;
    private String appName;
    private String apiPath;
    private String apiDesc;
    private String apiRemark;
    private GrpcApiParam requestParam;
    private GrpcApiParam responseParam;
    private String updateMsg;
    private String apiErrorCodes;
    private String updateUserName;
}
