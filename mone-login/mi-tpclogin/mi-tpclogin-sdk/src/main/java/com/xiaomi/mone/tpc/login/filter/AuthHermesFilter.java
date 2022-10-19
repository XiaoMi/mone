package com.xiaomi.mone.tpc.login.filter;

import com.google.gson.reflect.TypeToken;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.util.HttpClientUtil;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.infra.rpc.Result;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 19:16
 */
public class AuthHermesFilter implements Filter {

    private String hermesUrl;
    private String hermesProjectName = null;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        hermesUrl = filterConfig.getInitParameter(ConstUtil.hermesUrl);
        hermesProjectName = filterConfig.getInitParameter(ConstUtil.hermesProjectName);
        if (StringUtils.isBlank(hermesUrl) || StringUtils.isBlank(hermesProjectName)) {
            throw new IllegalArgumentException("hermes arg error");
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        AuthUserVo userVo = UserUtil.getUser();
        if (userVo == null) {
            return;
        }
        Map<String, String> getParams = new HashMap<>();
        getParams.put("userName", userVo.genFullAccount());
        getParams.put("projectName", hermesProjectName);
        getParams.put("resourceName", request.getMethod());
        if (StringUtils.isNotBlank(userVo.getName())) {
            getParams.put("cname", userVo.getName());
        } else {
            StringBuilder cname = new StringBuilder();
            cname.append(userVo.getName()).append("(").append(UserTypeEnum.getEnum(userVo.getUserType()).getDesc()).append(")");
            getParams.put("cname", cname.toString());
        }
        if (StringUtils.isNotBlank(userVo.getEmail())) {
            getParams.put("email", userVo.getEmail());
        } else {
            getParams.put("email", "mock@xiaomi.com");
        }
        Result<Boolean> resultVo = HttpClientUtil.doHttpGet(hermesUrl, getParams, new TypeToken<Result<Boolean>>(){});
        if (resultVo == null || !Boolean.TRUE.equals(resultVo.getData())) {
            response.sendError(403, "Forbidden");
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
