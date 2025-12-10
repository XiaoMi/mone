package com.xiaomi.mone.tpc.login.filter;

import com.xiaomi.mone.tpc.login.util.*;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import jakarta.servlet.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

class AuthTokenFilterV2 implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilterV2.class);

    private String[] ignoreUrls = null;
    private String loginUrl = null;

    public AuthTokenFilterV2() {
        HttpClientUtil.init();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        loginUrl = filterConfig.getInitParameter(ConstUtil.loginUrl);
        String authTokenUrl = filterConfig.getInitParameter(ConstUtil.authTokenUrl);
        if (StringUtils.isBlank(authTokenUrl)) {
            throw new IllegalArgumentException("authTokenUrl值为空");
        }
        ConstUtil.authTokenUrlVal = authTokenUrl;
        logger.info("auth_token_url is {}", authTokenUrl);
        String ignoreUrl = filterConfig.getInitParameter(ConstUtil.ignoreUrl);
        if (ignoreUrl != null && !"".equals(ignoreUrl)) {
            ignoreUrls = ignoreUrl.split(",");
        }
        logger.info("ignore_url_list is {}", ignoreUrls);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        String uri = request.getRequestURI();
        AuthTokenVo authToken = TokenUtil.parseAuthToken(request);
        logger.info("authToken={}", authToken);
        if (authToken == null) {
            if (CommonUtil.isIgnoreUrl(ignoreUrls, uri)) {
                logger.info("request is ignore_uri={}", uri);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            logger.info("request not login request_uri={}", uri);
            noAuthResponse(servletResponse);
            return;
        }
        ResultVo<AuthUserVo> resultVo = SystemReqUtil.authRequest(authToken.getAuthToken(), !authToken.isFromCookie());
        logger.info("getResult={}", resultVo);
        if (resultVo == null || !resultVo.success()) {
            if (CommonUtil.isIgnoreUrl(ignoreUrls, uri)) {
                logger.info("request is ignore_uri={}", uri);
                filterChain.doFilter(servletRequest, servletResponse);
                return;
            }
            logger.info("request not login request_uri={}", uri);
            noAuthResponse(servletResponse);
            return;
        }
        if (!authToken.isFromCookie()) {
            setCookie(request, resultVo.getData(), servletResponse);
        }
        servletRequest.setAttribute(ConstUtil.TPC_USER, resultVo.getData());
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    /**
     * 无权限
     * @param servletResponse
     */
    private void noAuthResponse(ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        response.setStatus(401);
        response.setHeader(ConstUtil.AUTH_TOKEN, "1");
        response.setHeader(ConstUtil.loginUrl, loginUrl);
    }


    public static void setCookie(HttpServletRequest request, AuthUserVo userVo, ServletResponse servletResponse) throws IOException {
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        Cookie cookie = new Cookie(ConstUtil.AUTH_TOKEN, userVo.getToken());
        cookie.setPath("/");
        cookie.setMaxAge(userVo.getExprTime());
        String origin = Optional.ofNullable(request.getHeader("Origin")).orElse(request.getHeader("Host"));
        String domain = HostUtil.getDomain(origin);
        if (StringUtils.isNotBlank(domain)) {
            cookie.setDomain(domain);
        }
        response.addCookie(cookie);
    }

}
