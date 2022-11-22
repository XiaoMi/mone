package com.xiaomi.youpin.gwdash.interceptor;

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.TenantUtils;
import com.xiaomi.youpin.gwdash.context.TenementContext;
import com.xiaomi.youpin.gwdash.service.LoginService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author goodjava@qq.com
 */
@Aspect
@Component
@Slf4j
public class TenantInterceptor {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TenantUtils tenantUtils;

    @Around("execution(* com.xiaomi.youpin.gwdash.controller.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result = null;
        try {
            before();
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        } finally {
            after();
        }
        return result;
    }

    private void before() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String uri = request.getRequestURI();
            log.info("uri:{}", uri);
            if (uri.endsWith("/tenement/get") || uri.endsWith("/tenement/set")) {
                return;
            }

            if (uri.startsWith("/open/v1/private/api")) {
                SessionAccount account = loginService.getAccountFromSession(request, true);
                if (null != account) {
                    String tenant = tenantUtils.getTenant(request);
                    account.setTenant(tenant);
                }
                TenementContext.getContext().set(account);
            } else {
                SessionAccount account = loginService.getAccountFromSession();
                if (null != account) {
                    String tenant = tenantUtils.getTenant(request);
                    account.setTenant(tenant);
                }
                TenementContext.getContext().set(account);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private void after() {
        try {
            TenementContext.getContext().close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
