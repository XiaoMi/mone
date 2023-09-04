package com.xiaomi.mone.tpc.login.filter;

import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.Map;

/**
 * http请求拦截
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 15:38
 */
public class DoceanReqUserFilter extends DoceanFilter {

    private static final Logger log = LoggerFactory.getLogger(DoceanReqUserFilter.class);
    private DoceanFilter casFilter = null;
    private DoceanFilter tokenFilter = null;
    private boolean devMode;
    private boolean innerAuth;
    private String userInfoPath;

    @Override
    public void init(Map<String, String> filterConfig) {
        devMode = Boolean.parseBoolean(filterConfig.get(ConstUtil.devMode));
        innerAuth = Boolean.parseBoolean(filterConfig.get(ConstUtil.innerAuth));
        userInfoPath = filterConfig.get(ConstUtil.USER_INFO_PATH);
        if (StringUtils.isBlank(userInfoPath)) {
            userInfoPath = "/login/userinfo";
        }
        if (innerAuth) {
            casFilter = new DoceanAuthCasFilter();
            casFilter.init(filterConfig);
        } else {
            tokenFilter = new DoceanAuthTokenFilter();
            tokenFilter.init(filterConfig);
        }
    }

    @Override
    public boolean doFilter(MvcContext mvcContext) {
        //开发模式使用
        if (devMode) {
            String mockAcc = mvcContext.getHeaders().get("user");
            if (StringUtils.isBlank(mockAcc)) {
                mockAcc = "test";
            }
            String mockAccType = mvcContext.getHeaders().get("userType");
            if (StringUtils.isBlank(mockAccType)) {
                mockAccType = UserTypeEnum.CAS_TYPE.getCode() + "";
            }
            AuthUserVo authUserVo = new AuthUserVo();
            authUserVo.setUserType(Integer.parseInt(mockAccType));
            authUserVo.setAccount(mockAcc);
            authUserVo.setName(mockAcc);
            mvcContext.session().setAttribute(ConstUtil.TPC_USER, authUserVo);
            return true;
        }
        boolean result = false;
        if (innerAuth) {
            result = casFilter.doFilter(mvcContext);
        } else {
            result = tokenFilter.doFilter(mvcContext);
        }
        return result;
    }

    @Override
    public void destroy() {
        if (casFilter != null) {
            casFilter.destroy();
        }
        if (tokenFilter != null) {
            tokenFilter.destroy();
        }
    }

}
