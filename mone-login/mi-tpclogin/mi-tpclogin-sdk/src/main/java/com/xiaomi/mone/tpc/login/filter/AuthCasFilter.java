package com.xiaomi.mone.tpc.login.filter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.CommonUtil;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.util.SignUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 10:08
 */
public class AuthCasFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(AuthCasFilter.class);
    private String[] publicKeys = null;
    private String[] ignoreUrls;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String publicKeyStr = filterConfig.getInitParameter(ConstUtil.PUBLIC_KEY_FILTER_INIT_PARAM_KEY);
        if (StringUtils.isBlank(publicKeyStr)) {
            throw new IllegalArgumentException("CAS_PUBLIC_KEY must config");
        }
        publicKeys = publicKeyStr.split("[,|，]");

        String ignoreUrl = filterConfig.getInitParameter(ConstUtil.ignoreUrl);
        if (StringUtils.isNotBlank(ignoreUrl)) {
            ignoreUrls = ignoreUrl.split("[,|，]");
            log.info("已设置忽略路径，ignoreUrls:{}", ignoreUrl);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            if (log.isDebugEnabled()) {
                httpHeaderLog(httpServletRequest);
            }
            String url = ((HttpServletRequest) request).getRequestURI();
            log.info("AuthCasFilter begin filter url:{}", url);
            if (CommonUtil.isIgnoreUrl(ignoreUrls, url)) {
                log.info("配置为忽略的路径,url:{}", url);
                chain.doFilter(request, response);
                return;
            }
            String verifyIdentitySignData = httpServletRequest.getHeader(ConstUtil.HEADER_KEY_SIGN_VERIFY_IDENTITY);
            if (StringUtils.isEmpty(verifyIdentitySignData)) {
                log.error("没有标识身份的签名数据,url:{}", url);
                noAuthResponse(response);
                return;
            }
            String currentUsePublicKey = null;
            String verifyIdentityData = null;
            for (String key : publicKeys) {
                verifyIdentityData = SignUtil.verifySignGetInfo(verifyIdentitySignData, key);
                if (StringUtils.isNotEmpty(verifyIdentityData)) {
                    currentUsePublicKey = key;
                    break;
                }
            }
            if (StringUtils.isEmpty(verifyIdentityData)) {
                log.error("检测身份,验签失败,url:{},signData:{}", url, verifyIdentitySignData);
                noAuthResponse(response);
                return;
            }
            log.info("账号登录,url:{}", url);
            String signAndUserSignData = httpServletRequest.getHeader(ConstUtil.HEADER_KEY_SIGN_AND_USER_DATA);
            //没有签名信息不做验签
            if (StringUtils.isEmpty(signAndUserSignData)) {
                log.info("确认请求，没有签名用户数据(bypass|静态资源)，url:{}", url);
                chain.doFilter(request, response);
                return;
            }
            //验签，获取用户数据
            String userJson = SignUtil.verifySignGetInfo(signAndUserSignData, currentUsePublicKey);
            //验签失败
            if (StringUtils.isEmpty(userJson)) {
                log.error("获取用户数据，验签失败,url:{},signData:{}", url, signAndUserSignData);
                noAuthResponse(response);
                return;
            }
            Gson gson = new GsonBuilder().serializeNulls().create();
            UserInfoVO userInfo = gson.fromJson(userJson, UserInfoVO.class);
            AuthUserVo authUserVo = new AuthUserVo();
            authUserVo.setUserType(UserTypeEnum.CAS_TYPE.getCode());
            authUserVo.setAccount(userInfo.getUser());
            authUserVo.setName(userInfo.getDisplayName());
            authUserVo.setEmail(userInfo.getEmail());
            authUserVo.setAvatarUrl(userInfo.getAvatar());
            authUserVo.setCasUid(userInfo.getuID());
            authUserVo.setDepartmentName(userInfo.getDepartmentName());
            request.setAttribute(ConstUtil.TPC_USER, authUserVo);
            log.info("AuthCasFilter check success,url:{}", url);
            chain.doFilter(request, response);
        } catch (Throwable t) {
            log.error("AuthCasFilter check exception", t);
            throw new ServletException(t);
        }
    }

    /**
     * 返回无权限
     */
    private void noAuthResponse(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setStatus(401);
    }

    /**
     * log所有http header
     */
    private void httpHeaderLog(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            map.put(header, request.getHeader(header));
        }
        Gson gson = new Gson();
        log.debug("http all header>>>>{}", gson.toJson(map));
    }

    @Override
    public void destroy() {
        log.info("AuthCasFilter destroy finish..");
    }

}
