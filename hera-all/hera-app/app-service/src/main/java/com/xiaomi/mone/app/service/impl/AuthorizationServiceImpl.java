package com.xiaomi.mone.app.service.impl;

import com.xiaomi.mone.app.api.service.HeraAuthorizationApi;
import com.xiaomi.mone.app.api.service.HeraProjectGroupServiceApi;
import com.xiaomi.mone.app.auth.AuthorizationService;
import com.xiaomi.mone.app.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gaoxihui
 * @date 2023/6/16 5:21 下午
 */
@Slf4j
@Service(registry = "registryConfig", interfaceClass = HeraAuthorizationApi.class, group = "${dubbo.group}")
public class AuthorizationServiceImpl implements HeraAuthorizationApi {

    @Autowired
    AuthorizationService authorizationService;

    @Override
    public Result fetchToken(String user, String sign, Long timestamp) {
        return authorizationService.fetchToken(user,sign,timestamp);
    }

    @Override
    public Boolean checkAuthorization(String token) {
        return authorizationService.checkAuthorization(token);
    }
}
