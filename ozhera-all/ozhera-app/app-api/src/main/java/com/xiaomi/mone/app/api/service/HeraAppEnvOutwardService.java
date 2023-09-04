package com.xiaomi.mone.app.api.service;

import com.xiaomi.mone.app.api.model.HeraSimpleEnv;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/12 11:33
 */
public interface HeraAppEnvOutwardService {

    List<HeraSimpleEnv> querySimpleEnvAppBaseInfoId(Integer id);
}
