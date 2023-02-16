package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

/**
 * @author zhangjuan
 * @Description 从 trace 得到的 matrix 应用信息
 * @date 2022-06-17
 */
@Data
public class TraceAppInfoDTO {
    private String appName;
    private Long iamTreeId;
    private String matrixDeploySpace;

    public TraceAppInfoDTO(String appName, Long iamTreeId, String matrixDeploySpace) {
        this.setAppName(appName);
        this.setIamTreeId(iamTreeId);
        this.setMatrixDeploySpace(matrixDeploySpace);
    }
}
