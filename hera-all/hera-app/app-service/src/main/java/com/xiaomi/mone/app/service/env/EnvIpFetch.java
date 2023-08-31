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

    HeraAppEnvVo fetch(Long appBaseId, Long appId, String appName) throws Exception;

    default HeraAppEnvVo buildHeraAppEnvVo(Long appBaseId, Long appId, String appName, List<HeraAppEnvVo.EnvVo> envVos) {
        return HeraAppEnvVo.builder().heraAppId(appBaseId)
                .appId(appId)
                .appName(appName)
                .envVos(envVos)
                .build();
    }
}
