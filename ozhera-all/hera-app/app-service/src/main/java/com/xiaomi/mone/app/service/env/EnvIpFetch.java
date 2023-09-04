package com.xiaomi.mone.app.service.env;

import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/29 16:18
 */
public interface EnvIpFetch {

    String SERVER_PREFIX = "prometheus_server";

    String ENV_NAME = "env_name";

    String ENV_ID = "env_id";

    String DEFAULT_EVN_ID = "0";
    String DEFAULT_EVN_NAME = "default_env";


    HeraAppEnvVo fetch(Long appBaseId, Long appId, String appName) throws Exception;

    default HeraAppEnvVo buildHeraAppEnvVo(Long appBaseId, Long appId, String appName, List<HeraAppEnvVo.EnvVo> envVos) {
        return HeraAppEnvVo.builder().heraAppId(appBaseId)
                .appId(appId)
                .appName(appName)
                .envVos(envVos)
                .build();
    }
}
