package com.xiaomi.mone.tpc.login.filter;

import com.xiaomi.mone.tpc.login.util.*;
import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class DoceanAuthTokenFilter extends DoceanFilter {

    private static final Logger logger = LoggerFactory.getLogger(DoceanAuthTokenFilter.class);

    private String[] ignoreUrls = null;
    private String loginUrl = null;

    public DoceanAuthTokenFilter() {
        HttpClientUtil.init();
    }

    @Override
    public void init(Map<String, String> filterConfig) {
        loginUrl = filterConfig.get(ConstUtil.loginUrl);
        String authTokenUrl = filterConfig.get(ConstUtil.authTokenUrl);
        if (StringUtils.isBlank(authTokenUrl)) {
            throw new IllegalArgumentException("authTokenUrl值为空");
        }
        ConstUtil.authTokenUrlVal = authTokenUrl;
        logger.info("auth_token_url is {}", authTokenUrl);
        String ignoreUrl = filterConfig.get(ConstUtil.ignoreUrl);
        if (ignoreUrl != null && !"".equals(ignoreUrl)) {
            ignoreUrls = ignoreUrl.split(",");
        }
        logger.info("ignore_url_list is {}", ignoreUrls);
    }

    @Override
    public boolean doFilter(MvcContext mvcContext) {
        String url = mvcContext.getPath();
        AuthTokenVo authToken = DoceanTokenUtil.parseAuthToken(mvcContext);
        logger.info("authToken={}", authToken);
        if (authToken == null) {
            if (CommonUtil.isIgnoreUrl(ignoreUrls, url)) {
                logger.info("request is ignore_url={}", url);
                return true;
            }
            logger.info("request not login request_url={}", url);
            noAuthResponse(mvcContext);
            return false;
        }
        ResultVo<AuthUserVo> resultVo = SystemReqUtil.authRequest(authToken.getAuthToken(), !authToken.isFromCookie());
        logger.info("getResult={}", resultVo);
        if (resultVo == null || !resultVo.success()) {
            if (CommonUtil.isIgnoreUrl(ignoreUrls, url)) {
                logger.info("request is ignore_url={}", url);
                return true;
            }
            logger.info("request not login request_url={}", url);
            noAuthResponse(mvcContext);
            return false;
        }
        if (!authToken.isFromCookie()) {
            DoceanTokenUtil.setCookie(resultVo.getData(), mvcContext);
        }
        mvcContext.session().setAttribute(ConstUtil.TPC_USER, resultVo.getData());
        return true;
    }

    /**
     * 无权限
     * @param mvcContext
     */
    private void noAuthResponse(MvcContext mvcContext) {
        mvcContext.getResHeaders().put("x-status", "401");
        mvcContext.getResHeaders().put(ConstUtil.AUTH_TOKEN, "1");
        mvcContext.getResHeaders().put(ConstUtil.loginUrl, loginUrl);
    }

    @Override
    public void destroy() {

    }

}
