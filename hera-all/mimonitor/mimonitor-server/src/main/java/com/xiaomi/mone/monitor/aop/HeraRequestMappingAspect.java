package com.xiaomi.mone.monitor.aop;

import com.xiaomi.mone.monitor.bo.HeraReqInfo;
import com.xiaomi.mone.monitor.service.aop.action.HeraRequestMappingAction;
import com.xiaomi.mone.monitor.service.aop.context.HeraRequestMappingContext;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @project: mimonitor
 * @author: zgf1
 * @date: 2022/1/13 14:39
 */
@Slf4j
@Aspect
@Configuration
public class HeraRequestMappingAspect {

    @Pointcut("@annotation(com.xiaomi.mone.monitor.aop.HeraRequestMapping)")
    public void operationLog(){}

    @Autowired
    private ApplicationContext applicationContext;

    @Resource(name = "heraRequestMappingExecutor")
    private ThreadPoolExecutor heraRequestMappingExecutor;

    @Bean("heraRequestMappingExecutor")
    public ThreadPoolExecutor heraRequestMappingExecutor() {
        return new ThreadPoolExecutor(1, 20, 5, TimeUnit.MINUTES, new LinkedBlockingQueue(20),
                (Runnable r) -> new Thread(r, "compute-execute-thread-v2"), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Around("operationLog()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            Object[] args = joinPoint.getArgs();
            if (args == null) {
                args = new Object[0];
            }
            String reqUrl = null;
            String user = null;
            for (Object arg : args) {
                if (arg instanceof HttpServletRequest) {
                    reqUrl = ((HttpServletRequest)arg).getServletPath();
                 //   UserInfoVO userInfo = AegisFacade.getUserInfo((HttpServletRequest)arg);
                    AuthUserVo userInfo = UserUtil.getUser();
                    if (userInfo != null) {
                        user = userInfo.genFullAccount();
                    }
                    break;
                }
            }
            HeraRequestMapping anno = method.getAnnotation(HeraRequestMapping.class);
            HeraReqInfo heraReqInfo = HeraReqInfo.builder().reqUrl(reqUrl).user(user)
                    .moduleName(anno.interfaceName().getModuleName().getCode())
                    .interfaceName(anno.interfaceName().getCode()).build();
            Object beanObj = applicationContext.getBean(anno.actionClass());
            if (beanObj instanceof HeraRequestMappingAction) {
                HeraRequestMappingAction beanAction = (HeraRequestMappingAction)beanObj;
                beanAction.beforeAction(args, heraReqInfo);
                Object result = null;
                try {
                    result = joinPoint.proceed();
                    return result;
                } finally {
                    if (heraReqInfo.getOperLog() != null && heraReqInfo.getOperLog().getId() != null) {
                        final Object aResult = result;
                        final Object[] aArgs = args;
                        final Map<String, Object> map = HeraRequestMappingContext.getAll();
                        heraRequestMappingExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                HeraRequestMappingContext.putAll(map);
                                try {
                                    beanAction.afterAction(aArgs, heraReqInfo, aResult);
                                } finally {
                                    HeraRequestMappingContext.clearAll();
                                }
                            }
                        });
                    }
                }
            }
            return joinPoint.proceed();
        } finally {
            HeraRequestMappingContext.clearAll();
        }
    }
}
