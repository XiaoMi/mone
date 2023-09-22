package com.xiaomi.mone.monitor.controller;

import com.xiaomi.mone.app.api.service.HeraAuthorizationApi;
import com.xiaomi.mone.app.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author gaoxihui
 * @date 2023/6/20 9:46 上午
 */
@Slf4j
@RestController
public class AuthorizationController {

    @Reference(registry = "registryConfig", check = false, interfaceClass = HeraAuthorizationApi.class, group = "${dubbo.group.heraapp}",timeout = 2000)
    HeraAuthorizationApi heraAuthorizationApi;

    @PostMapping("/api/getToken")
    @ResponseBody
    public Result getToken(@RequestBody Map<String, Object> map){
        log.info("getToken param map : {}",map);
        String userName = (String) map.get("userName");
        String sign = (String) map.get("sign");
        Long timestamp = (Long) map.get("timestamp");
        return heraAuthorizationApi.fetchToken(userName, sign, timestamp);
    }


}
