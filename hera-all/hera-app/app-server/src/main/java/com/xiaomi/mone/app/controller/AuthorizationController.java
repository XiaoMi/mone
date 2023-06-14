package com.xiaomi.mone.app.controller;


import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.client.config.utils.MD5;
import com.xiaomi.mone.app.auth.AuthorizationService;
import com.xiaomi.mone.app.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gaoxihui
 * @date 2023/6/12 4:02 下午
 */
@RestController
public class AuthorizationController {

    @Autowired
    AuthorizationService authorizationService;

    @PostMapping
    public Result fetchToken(String user, String sign, Long timestamp){
        return authorizationService.fetchToken(user, sign, timestamp);
    }

}
