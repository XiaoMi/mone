package com.xiaomi.mone.app.service;

import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import com.xiaomi.mone.app.model.vo.HeraAppOperateVo;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/9 17:46
 */
public interface HeraAppEnvService {

    HeraAppEnvVo queryAppEnvById(Long id);

    Long addAppEnv(HeraAppOperateVo operateVo);

    Long updateAppEnv(HeraAppOperateVo operateVo);

    Boolean deleteAppEnv(Long id);

    void fetchIpsOpByApp(String app);

    void addAppEnvNotExist(HeraAppEnvVo heraAppEnvVo);

    Boolean addAppEnvNotExist(HeraAppEnvVo heraAppEnvVo, HeraAppEnvVo.EnvVo envVo);
}
