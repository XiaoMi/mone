package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    public static void setCookie(AuthUserVo userVo, ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        Cookie cookie = new Cookie(ConstUtil.AUTH_TOKEN, userVo.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(userVo.getExprTime());
        response.addCookie(cookie);
    }

    public static final AuthTokenVo parseAuthToken(HttpServletRequest request) {
        String authToken = request.getParameter(ConstUtil.AUTH_TOKEN);
        if (StringUtils.isNotBlank(authToken)) {
            AuthTokenVo tokenVo = new AuthTokenVo();
            tokenVo.setAuthToken(authToken);
            tokenVo.setFromCookie(false);
            return tokenVo;
        }
        String referer = request.getHeader("Referer");
        logger.info("referer={}", referer);
        if (StringUtils.isBlank(referer)) {
            return getAuthToken(request);
        }
        String[] arr = referer.split("\\?");
        if (arr.length != 2) {
            return getAuthToken(request);
        }
        Map<String, String> params = new HashMap<>();
        String[] kvArr = arr[1].split("\\&");
        for (String kv : kvArr) {
            String[] subKvArr = kv.split("\\=");
            if (subKvArr.length != 2) {
                continue;
            }
            if (!subKvArr[0].equals(ConstUtil.AUTH_TOKEN)) {
                continue;
            }
            AuthTokenVo tokenVo = new AuthTokenVo();
            tokenVo.setAuthToken(subKvArr[1]);
            tokenVo.setFromCookie(false);
            return tokenVo;
        }
        return getAuthToken(request);
    }

    /**
     * 获取cookie值
     * @param servletRequest
     * @return
     */
    private static final AuthTokenVo getAuthToken(ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        if (request == null || request.getCookies() == null || request.getCookies().length == 0) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if (cookie.getName().equals(ConstUtil.AUTH_TOKEN)) {
                AuthTokenVo tokenVo = new AuthTokenVo();
                tokenVo.setAuthToken(cookie.getValue());
                tokenVo.setFromCookie(true);
                return tokenVo;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Throwable {
        AuthUserVo authUser = new AuthUserVo();
        authUser.setToken("3333");
        setCookieUrl(authUser, "https://127.0.0.1/#/mifaas-detail?funId=57");

    }

    /**
     * cookie设置跳转
     * @param userVo
     * @param pageUrl
     * @throws Throwable
     */
    public static void setCookieUrl(AuthUserVo userVo, String pageUrl) throws Throwable {
        StringBuilder tokenArg = new StringBuilder();
        tokenArg.append(ConstUtil.AUTH_TOKEN).append("=").append(userVo.getToken());
        StringBuilder setCookieUrl = new StringBuilder();
        int pos = pageUrl.indexOf(ConstUtil.AUTH_TOKEN);
        if (pos > 0) {
            setCookieUrl.append(pageUrl.substring(0, pos));
            setCookieUrl.append(tokenArg);
            String subPageUrl = pageUrl.substring(pos);
            pos = subPageUrl.indexOf('#');
            if (pos > 0) {
                setCookieUrl.append(subPageUrl.substring(pos));
                userVo.setSetCookUrl(setCookieUrl.toString());
                return;
            }
            pos = subPageUrl.indexOf('&');
            if (pos > 0) {
                setCookieUrl.append(subPageUrl.substring(pos));
                userVo.setSetCookUrl(setCookieUrl.toString());
                return;
            }
            userVo.setSetCookUrl(setCookieUrl.toString());
            return;
        }
        pos = pageUrl.indexOf('#');
        if (pos > 0) {
            setCookieUrl.append(pageUrl.substring(0, pos));
            setCookieUrl.append("?");
            setCookieUrl.append(tokenArg);
            setCookieUrl.append(pageUrl.substring(pos));
        } else {
            setCookieUrl.append(pageUrl);
            if(pageUrl.contains("?")) {
                setCookieUrl.append("&");
                setCookieUrl.append(tokenArg);
            } else {
                setCookieUrl.append("?");
                setCookieUrl.append(tokenArg);
            }
        }
        userVo.setSetCookUrl(setCookieUrl.toString());
    }

}
