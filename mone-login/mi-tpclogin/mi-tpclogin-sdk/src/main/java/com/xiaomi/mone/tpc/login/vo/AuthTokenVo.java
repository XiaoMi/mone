package com.xiaomi.mone.tpc.login.vo;

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
