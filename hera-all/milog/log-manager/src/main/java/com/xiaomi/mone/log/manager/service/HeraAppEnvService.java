package com.xiaomi.mone.log.manager.service;

import com.xiaomi.mone.app.api.model.HeraSimpleEnv;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/12 11:47
 */
public interface HeraAppEnvService {

    List<HeraSimpleEnv> querySimpleEnvAppBaseInfoId(Integer appBaseId);
}
