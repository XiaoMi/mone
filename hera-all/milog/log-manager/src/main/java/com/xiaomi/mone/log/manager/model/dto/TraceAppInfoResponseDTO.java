package com.xiaomi.mone.log.manager.model.dto;

import lombok.Data;

import java.util.List;

/**
 * @author zhangjuan
 * @Description 调 trace 接口查应用信息的返回
 * @date 2022-06-17
 */
@Data
public class TraceAppInfoResponseDTO {
    private int total;
    private TraceAppInfoData data;

    @Data
    public static class TraceAppInfoData {
        private Long appId;
        private String appName;
        private String deployPlatform;
        private List<TraceAppService> services;
    }

    @Data
    public static class TraceAppService {
        private Long iamTreeId;
        private String extraConfigJson;
        private TraceAppExtra extraConfig;
    }

    @Data
    public static class TraceAppExtra {
        private String deployInfo;
        private ExtraDeployInfo deployInfoObject;
    }

    @Data
    public static class ExtraDeployInfo {
        private String matrixDeploySpace;
    }
}
