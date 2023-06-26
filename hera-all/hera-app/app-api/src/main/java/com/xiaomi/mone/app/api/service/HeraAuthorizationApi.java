package com.xiaomi.mone.app.api.service;

import com.xiaomi.mone.app.common.Result;

/**
 * @author gaoxihui
 * @date 2023/6/16 5:18 下午
 */
public interface HeraAuthorizationApi {

    Result fetchToken(String user, String sign, Long timestamp);

    Result checkAuthorization(String token);
}
