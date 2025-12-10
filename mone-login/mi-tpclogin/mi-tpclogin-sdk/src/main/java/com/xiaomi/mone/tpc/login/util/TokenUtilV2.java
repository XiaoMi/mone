package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TokenUtilV2 {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtilV2.class);

    /**
     * 给退出url设置token
     * @param authToken
     * @param logoutUrl
     * @throws Throwable
     */
    public static String getLogoutUrlWithToken(String authToken, String logoutUrl) {
        if (StringUtils.isBlank(authToken) || StringUtils.isBlank(logoutUrl)) {
            logger.error("getLogoutUrlWithToken authToken={}, logoutUrl={}", authToken, logoutUrl);
            return null;
        }
        try {
            StringBuilder pageUrl = new StringBuilder();
            pageUrl.append(logoutUrl);
            URL url = new URL(logoutUrl);
            String params = url.getQuery();
            if (StringUtils.isBlank(params)) {
                if (logoutUrl.endsWith("?")) {
                    pageUrl.append(ConstUtil.AUTH_TOKEN).append("=").append(authToken);
                } else {
                    pageUrl.append("?").append(ConstUtil.AUTH_TOKEN).append("=").append(authToken);
                }
            } else {
                pageUrl.append("&").append(ConstUtil.AUTH_TOKEN).append("=").append(authToken);
            }
            return pageUrl.toString();
        } catch (Throwable e) {
            logger.error("getLogoutUrlWithToken authToken={}, logoutUrl={}", authToken, logoutUrl, e);
            return null;
        }
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

}
