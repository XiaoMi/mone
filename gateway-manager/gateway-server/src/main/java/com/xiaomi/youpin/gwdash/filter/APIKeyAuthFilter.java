package com.xiaomi.youpin.gwdash.filter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.xiaomi.youpin.gwdash.common.Consts.SKIP_MI_DUN_USER_NAME;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/2/28
 */
public class APIKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final String principalRequestHeader;

    public APIKeyAuthFilter(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        return request.getHeader(principalRequestHeader);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException, AuthenticationServiceException {
        String username = ((HttpServletRequest) request).getHeader(SKIP_MI_DUN_USER_NAME);

        if (StringUtils.isBlank(username)) {
            throw new AuthenticationServiceException("username is blank");
        }
        super.doFilter(request, response, chain);
    }
}