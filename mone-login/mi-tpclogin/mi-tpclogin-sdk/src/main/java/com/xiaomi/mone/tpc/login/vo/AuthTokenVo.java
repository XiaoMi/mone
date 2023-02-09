package com.xiaomi.mone.tpc.login.vo;

import com.xiaomi.mone.tpc.login.util.ConstUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/6/14 18:58
 */
public class AuthTokenVo {
    private String authToken;
    private boolean fromCookie;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean isFromCookie() {
        return fromCookie;
    }

    public void setFromCookie(boolean fromCookie) {
        this.fromCookie = fromCookie;
    }

    @Override
    public String toString() {
        return "ParseAuthTokenVo{" +
                "authToken='" + authToken + '\'' +
                ", fromCookie=" + fromCookie +
                '}';
    }
}
