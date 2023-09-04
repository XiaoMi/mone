package com.xiaomi.mone.app.controller;

import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import com.xiaomi.mone.app.service.env.DefaultEnvIpFetch;
import com.xiaomi.mone.app.service.env.DefaultHttpEnvIpFetch;
import com.xiaomi.mone.app.service.env.EnvIpFetch;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/17 10:00
 */
@RestController
@RequestMapping("/test")
@ConditionalOnProperty(name = "service.selector.property", havingValue = "outer")
public class TestController {

    @Resource
    private DefaultEnvIpFetch defaultEnvIpFetch;

    @Resource
    private DefaultHttpEnvIpFetch defaultHttpEnvIpFetch;

    @GetMapping("/env/ip/fetch/{appId}")
    public EnvIpFetch getEnvIpFetch(@PathVariable String appId) {
        return defaultEnvIpFetch.getEnvFetch(appId);
    }


    @GetMapping("/hera/app/env")
    public HeraAppEnvVo getHeraAppEnvVo(Long appBaseId, Long appId, String appName) throws Exception {
        return defaultHttpEnvIpFetch.fetch(appBaseId, appId, appName);
    }
}
