package com.xiaomi.mone.tpc.login.filter;


import com.xiaomi.mone.tpc.login.anno.AuthCheck;
import com.xiaomi.mone.tpc.login.enums.RpcTypeEnum;
import com.xiaomi.mone.tpc.login.util.*;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.MoneTpcContext;
import com.xiaomi.mone.tpc.login.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 必须为spring bean
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/1/13 14:39
 */
@Aspect
public class RpcReqUserFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private String authTokenUrl;
    private Object authFailResult;
    private Class authFailResultCls;
    private List<String> supportSysNames;

    public RpcReqUserFilter(String authTokenUrl) {
        this(authTokenUrl, null);
    }

    public RpcReqUserFilter(String authTokenUrl, Object authFailResult) {
        if (StringUtils.isBlank(authTokenUrl)) {
            new RpcResult(new IllegalArgumentException("authTokenUrl is null"));
        }
        this.authTokenUrl = authTokenUrl;
        if (authFailResult != null) {
            this.authFailResult = authFailResult;
            this.authFailResultCls = authFailResult.getClass();
        }
    }

    public void setSupportSysNames(List<String> supportSysNames) {
        this.supportSysNames = supportSysNames;
    }

    @Pointcut("@annotation(com.xiaomi.mone.tpc.login.anno.AuthCheck)")
    public void authCheck(){
    }

    @Around("authCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        AuthCheck authCheck = method.getAnnotation(AuthCheck.class);
        if (authCheck == null || !authCheck.authSys()) {
            return joinPoint.proceed();
        }
        String sysName = null;
        String sysSign = null;
        String userToken = null;
        String reqTime = null;
        Object[] args = joinPoint.getArgs();
        MoneTpcContext context = null;
        String dataSign = null;
        if (args != null && args.length > 0) {
            context = Arrays.stream(args).filter(arg -> arg != null).filter(arg -> arg instanceof MoneTpcContext).map(arg -> (MoneTpcContext)arg).findFirst().orElse(null);
            if (context != null) {
                sysName = context.getSysName();
                sysSign = context.getSysSign();
                userToken = context.getUserToken();
                reqTime = String.valueOf(context.getReqTime());
                //防止请求透传数据
                context.setAccount(null);
                context.setUserType(null);
            }
            List<Object> list = Arrays.stream(args).filter(arg -> arg != null).filter(arg -> !(arg instanceof MoneTpcContext)).collect(Collectors.toList());
            dataSign =  SignUtil.getDataSign(list.toArray());
        }
        if (context == null && RpcTypeEnum.DUBBO.equals(authCheck.rpcType())) {
            sysName = RpcContext.getContext().getAttachment(ConstUtil.SYS_NAME);
            sysSign = RpcContext.getContext().getAttachment(ConstUtil.SYS_SIGN);
            userToken = RpcContext.getContext().getAttachment(ConstUtil.USER_TOKEN);
            reqTime = RpcContext.getContext().getAttachment(ConstUtil.REQ_TIME);
        } else if (context == null && RpcTypeEnum.HTTP.equals(authCheck.rpcType())) {
            sysName = HeaderUtil.getHeader(ConstUtil.SYS_NAME);
            sysSign = HeaderUtil.getHeader(ConstUtil.SYS_SIGN);
            userToken = HeaderUtil.getHeader(ConstUtil.USER_TOKEN);
            reqTime = HeaderUtil.getHeader(ConstUtil.REQ_TIME);
        }
        if (StringUtils.isBlank(sysName) || StringUtils.isBlank(sysSign) || StringUtils.isBlank(reqTime)) {
            throw new RuntimeException("user_auth_arg is null");
        }
        if (supportSysNames != null && !supportSysNames.isEmpty() && !supportSysNames.contains(sysName)) {
            throw new RuntimeException("sysName has no permission to call");
        }
        if (authCheck.authUser() && StringUtils.isBlank(userToken)) {
            throw new RuntimeException("userToken is null");
        }
        ResultVo<AuthUserVo> result = SystemReqUtil.authRequest(sysName, sysSign, userToken, reqTime, dataSign, authTokenUrl);
        if (result == null || !result.success()) {
            return getResult(method.getReturnType(), authFailResultCls, authFailResult, "user_auth_check is fail");
        }
        AuthUserVo userVo = result.getData();
        if (context != null && userVo != null) {
            context.setAccount(userVo.getAccount());
            context.setUserType(userVo.getUserType());
        }
        try {
            UserUtil.setUser(userVo);
            return joinPoint.proceed();
        } finally{
            UserUtil.clearUser();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            Enumeration<String> names = request.getHeaderNames();
            if (names != null) {
                while (names.hasMoreElements()) {
                    String key = names.nextElement();
                    HeaderUtil.addHeader(key, request.getHeader(key));
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);
        } finally{
            HeaderUtil.clearHeader();
        }
    }

    private Object getResult(Class<?> resultCls, Class<?> failResultCls, Object failResult, String message) {
        if (failResult != null && failResultCls != null && failResultCls.equals(resultCls)) {
            return failResult;
        }
        throw new RuntimeException(message);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
