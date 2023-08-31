package com.xiaomi.mone.tpc.login.filter;

import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.util.GsonUtil;
import com.xiaomi.mone.tpc.login.util.TokenUtil;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * http请求拦截
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:38
 */
public class HttpReqUserFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpReqUserFilter.class);
    private Filter casFilter = null;
    private Filter tokenFilter = null;
    private Filter hermesFilter = null;
    private boolean devMode;
    private boolean innerAuth;
    private String userInfoPath;
    private String logoutUrl;
    private String loginUrl;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logoutUrl = filterConfig.getInitParameter(ConstUtil.logoutUrl);
        loginUrl = filterConfig.getInitParameter(ConstUtil.loginUrl);
        devMode = Boolean.parseBoolean(filterConfig.getInitParameter(ConstUtil.devMode));
        innerAuth = Boolean.parseBoolean(filterConfig.getInitParameter(ConstUtil.innerAuth));
        userInfoPath = filterConfig.getInitParameter(ConstUtil.USER_INFO_PATH);
        if (StringUtils.isBlank(userInfoPath)) {
            userInfoPath = "/login/userinfo";
        }
        if (innerAuth) {
            casFilter = new AuthCasFilter();
            casFilter.init(filterConfig);
        } else {
            tokenFilter = new AuthTokenFilter();
            tokenFilter.init(filterConfig);
        }
        boolean openHermes = Boolean.parseBoolean(filterConfig.getInitParameter(ConstUtil.openHermes));
        if (openHermes) {
            hermesFilter = new AuthHermesFilter();
            hermesFilter.init(filterConfig);
        }
    }

    @Override
    public void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3) throws IOException, ServletException {
        //开发模式使用
        if (devMode) {
            String mockAcc = ((HttpServletRequest)var1).getHeader("user");
            if (StringUtils.isBlank(mockAcc)) {
                mockAcc = "test";
            }
            String mockAccType = ((HttpServletRequest)var1).getHeader("userType");
            UserTypeEnum userType = UserTypeEnum.getEnum(mockAccType);
            if (userType == null) {
                userType = UserTypeEnum.CAS_TYPE;
            }
            try {
                AuthUserVo authUserVo = new AuthUserVo();
                authUserVo.setUserType(userType.getCode());
                authUserVo.setAccount(mockAcc);
                authUserVo.setName(mockAcc);
                UserUtil.setUser(authUserVo);
                if (isUserRequest(var1)) {
                    writeUserResponse(var2);
                    return;
                }
                var3.doFilter(var1, var2);
            } finally{
                UserUtil.clearUser();
            }
            return;
        }
        FilterChain chain = new FilterChain() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse) throws IOException, ServletException {
                AuthUserVo userInfo = (AuthUserVo)servletRequest.getAttribute(ConstUtil.TPC_USER);
                if (userInfo == null) {
                    var3.doFilter(servletRequest, servletResponse);
                    return;
                }
                try {
                    UserUtil.setUser(userInfo);
                    if (isUserRequest(var1)) {
                        writeUserResponse(var2);
                        return;
                    }
                    if (hermesFilter != null) {
                        hermesFilter.doFilter(servletRequest, servletResponse, var3);
                    } else {
                        var3.doFilter(servletRequest, servletResponse);
                    }
                } finally {
                    UserUtil.clearUser();
                }
            }
        };
        if (innerAuth) {
            casFilter.doFilter(var1, var2, chain);
        } else {
            tokenFilter.doFilter(var1, var2, chain);
        }
    }

    private boolean isUserRequest(ServletRequest request) {
        String url = ((HttpServletRequest) request).getRequestURI();
        return url.equals(userInfoPath);
    }

    private void writeUserResponse(ServletResponse var2) throws IOException {
        AuthUserVo userInfo = UserUtil.getUser();
        if (userInfo == null) {
            return;
        }
        userInfo.setLoginUrl(loginUrl);
        if (UserTypeEnum.CAS_TYPE.getCode().equals(userInfo.getUserType())) {
            userInfo.setLogoutUrl(logoutUrl);
        } else {
            userInfo.setLogoutUrl(TokenUtil.getLogoutUrlWithToken(userInfo.getToken(), logoutUrl));
        }
        log.info("writeUserResponse.userInfo={}", userInfo);
        ResultVo<AuthUserVo> userResult = new ResultVo<>();
        userResult.setData(userInfo);
        userResult.setCode(0);
        userResult.setMessage("ok");
        var2.setCharacterEncoding("UTF-8");
        var2.setContentType("application/json;charset=utf-8");
        var2.getWriter().write(GsonUtil.gsonString(userResult));
    }

    @Override
    public void destroy() {
        if (casFilter != null) {
            casFilter.destroy();
        }
        if (tokenFilter != null) {
            tokenFilter.destroy();
        }
        if (hermesFilter != null) {
            hermesFilter.destroy();
        }
    }

}
