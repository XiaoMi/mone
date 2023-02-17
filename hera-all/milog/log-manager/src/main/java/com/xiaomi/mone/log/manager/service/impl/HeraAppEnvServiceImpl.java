package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.api.service.HeraAppEnvOutwardService;
import com.xiaomi.mone.log.manager.service.HeraAppEnvService;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Reference;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/12 11:48
 */
@Slf4j
@Service
public class HeraAppEnvServiceImpl implements HeraAppEnvService {

    @Reference(interfaceClass = HeraAppEnvOutwardService.class, group = "$dubbo.env.group", check = false)
    private HeraAppEnvOutwardService heraAppEnvOutwardService;

    @Override
    public List<HeraSimpleEnv> querySimpleEnvAppBaseInfoId(Integer appBaseId) {
        return heraAppEnvOutwardService.querySimpleEnvAppBaseInfoId(appBaseId);
    }
}
