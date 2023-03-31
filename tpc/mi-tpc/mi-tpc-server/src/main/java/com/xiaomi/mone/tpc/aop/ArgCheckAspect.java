package com.xiaomi.mone.tpc.aop;


import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.common.param.BaseParam;
import com.xiaomi.mone.tpc.common.vo.ResponseCode;
import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.mone.tpc.common.vo.UserVo;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.user.UserService;
import com.xiaomi.mone.tpc.util.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/1/13 14:39
 */
@Order
@Slf4j
@Aspect
@Configuration
public class ArgCheckAspect {

    @Autowired
    private UserService userService;

    @Pointcut("@annotation(com.xiaomi.mone.tpc.aop.ArgCheck)")
    public void argCheck(){}

    @Around("argCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length <= 0) {
            return joinPoint.proceed();
        }
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ArgCheck argCheck = method.getAnnotation(ArgCheck.class);
        //http请求标记
        RequestMapping reqMapping = method.getAnnotation(RequestMapping.class);
        Class<?> resultCls = method.getReturnType();
        for (Object arg : args) {
            if (!(arg instanceof com.xiaomi.mone.tpc.common.param.ArgCheck)) {
                continue;
            }
            if (arg instanceof BaseParam) {
                BaseParam baseParam = (BaseParam)arg;
                //不需要用户信息
                if (!argCheck.needUser()) {
                    //允许前端传递用户信息
                    if (!argCheck.allowArgUser()) {
                        baseParam.setAccount(null);
                        baseParam.setUserId(null);
                        baseParam.setUserType(null);
                    }
                } else {
                    AuthUserVo authUserVo = null;
                    if (reqMapping != null) {
                        authUserVo = UserUtil.getUser();
                        if (authUserVo == null) {
                            log.info("接口{}请求参数{},用户信息不存在", method.getName(), arg);
                            return getResult(resultCls, ResponseCode.ARG_ERROR);
                        }
                    } else {
                        if (StringUtils.isBlank(baseParam.getAccount())
                                || UserTypeEnum.getEnum(baseParam.getUserType()) == null) {
                            log.info("接口{}请求参数{},用户信息不存在", method.getName(), arg);
                            return getResult(resultCls, ResponseCode.ARG_ERROR);
                        }
                        authUserVo = new AuthUserVo();
                        authUserVo.setAccount(baseParam.getAccount());
                        authUserVo.setUserType(baseParam.getUserType());
                    }
                    UserVo userVo = userService.register(authUserVo.getAccount(), authUserVo.getUserType());
                    if (userVo == null) {
                        log.info("接口{}请求参数{},用户信息注册失败", method.getName(), arg);
                        return getResult(resultCls, ResponseCode.UNKNOWN_ERROR);
                    }
                    baseParam.setAccount(userVo.getAccount());
                    baseParam.setUserId(userVo.getId());
                    baseParam.setUserType(userVo.getType());
                }
            }
            log.info("接口{}请求参数{}", method.getName(), arg);
            if (!((com.xiaomi.mone.tpc.common.param.ArgCheck)arg).argCheck()) {
                log.warn("用户请求参数校验失败; arg={}", arg);
                return getResult(resultCls, ResponseCode.ARG_ERROR);
            }
        }
        try {
            Object result = joinPoint.proceed();
            log.info("接口{}请求响应{}", method.getName(), result);
            return result;
        } catch (Throwable e) {
            log.error("接口{}请求异常e={}", method.getName(), e.toString());
            return getResult(resultCls, ResponseCode.UNKNOWN_ERROR, e.getMessage());
        }
    }

    private Object getResult(Class<?> resultCls, ResponseCode responseCode) {
        if (resultCls.equals(ResultVo.class)) {
            return responseCode.build();
        } else {
            return ResultUtil.build(responseCode.build());
        }
    }

    private Object getResult(Class<?> resultCls, ResponseCode responseCode, String message) {
        if (resultCls.equals(ResultVo.class)) {
            return responseCode.build(message);
        } else {
            return ResultUtil.build(responseCode.build().getCode(), message);
        }
    }

}
