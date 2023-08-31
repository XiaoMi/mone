package com.xiaomi.mone.tpc.login.filter;


import com.xiaomi.mone.tpc.login.anno.ArgCheck;
import com.xiaomi.mone.tpc.login.enums.UserTypeEnum;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.mone.tpc.login.vo.BaseParam;
import com.xiaomi.mone.tpc.login.vo.MoneTpcContext;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/1/13 14:39
 */
@Aspect
public class ArgCheckFilter {

    private static final Logger log = LoggerFactory.getLogger(ArgCheckFilter.class);

    private Supplier<Object> noUserReturn;
    private Supplier<Object> argErrReturn;
    private Function<Throwable, Object> excpReturn;
    public ArgCheckFilter(Supplier<Object> noUserReturn, Supplier<Object> argErrReturn, Function<Throwable, Object> excpReturn) {
        if (noUserReturn == null || argErrReturn == null || excpReturn == null) {
            throw new IllegalArgumentException("参数错误");
        }
        this.noUserReturn = noUserReturn;
        this.argErrReturn = argErrReturn;
        this.excpReturn = excpReturn;
    }

    @Pointcut("@annotation(com.xiaomi.mone.tpc.login.anno.ArgCheck)")
    public void argCheck(){}

    @Around("argCheck()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        ArgCheck argCheck = method.getAnnotation(ArgCheck.class);
        boolean findUser = false;
        AuthUserVo authUserVo = null;
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof BaseParam) {
                    BaseParam baseParam = (BaseParam)arg;
                    if (!argCheck.allowArgUser()) {
                        baseParam.setAccount(null);
                        baseParam.setUserType(null);
                    }
                    authUserVo = UserUtil.getUser();
                    if (authUserVo != null) {
                        baseParam.setAccount(authUserVo.getAccount());
                        baseParam.setUserType(authUserVo.getUserType());
                    }
                    if (argCheck.checkUser()) {
                        if (StringUtils.isBlank(baseParam.getAccount())
                                || UserTypeEnum.getEnum(baseParam.getUserType()) == null) {
                            log.info("接口{}请求参数{},用户信息不存在", method.getName(), arg);
                            return noUserReturn.get();
                        }
                        findUser = true;
                    }
                } else if (arg instanceof MoneTpcContext) {
                    MoneTpcContext context = (MoneTpcContext)arg;
                    if (!argCheck.allowArgUser()) {
                        context.setAccount(null);
                        context.setUserType(null);
                    }
                    authUserVo = UserUtil.getUser();
                    if (authUserVo != null) {
                        context.setAccount(authUserVo.getAccount());
                        context.setUserType(authUserVo.getUserType());
                    }
                    if (argCheck.checkUser()) {
                        if (StringUtils.isBlank(context.getAccount())
                                || UserTypeEnum.getEnum(context.getUserType()) == null) {
                            log.info("接口{}请求参数{},用户信息不存在", method.getName(), arg);
                            return noUserReturn.get();
                        }
                        findUser = true;
                    }
                }
                if (arg instanceof com.xiaomi.mone.tpc.login.vo.ArgCheck) {
                    log.info("接口{}请求参数{}", method.getName(), arg);
                    if (!((com.xiaomi.mone.tpc.login.vo.ArgCheck)arg).argCheck()) {
                        log.warn("用户请求参数校验失败; arg={}", arg);
                        return argErrReturn.get();
                    }
                }
            }
        }
        if (!findUser && argCheck.checkUser()) {
            authUserVo = UserUtil.getUser();
            if (authUserVo == null) {
                log.info("接口{}请求参数{},用户信息不存在", method.getName());
                return noUserReturn.get();
            }
        }
        try {
            Object result = joinPoint.proceed();
            log.info("接口{}请求响应{}", method.getName(), result);
            return result;
        } catch (Throwable e) {
            log.error("接口{}请求异常e={}", method.getName(), e.toString());
            return excpReturn.apply(e);
        }
    }

}
