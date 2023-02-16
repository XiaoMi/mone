package com.xiaomi.mone.app.model.vo;

import com.xiaomi.mone.app.model.BaseCommon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/9 17:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeraAppEnvVo extends BaseCommon {

    private Long id;

    private Long heraAppId;

    private Long appId;

    private String appName;

    private List<EnvVo> envVos;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnvVo {
        private Long envId;

        private String envName;

        private List<String> ipList;
    }
}
