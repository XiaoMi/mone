package com.xiaomi.data.push.action;

import com.xiaomi.data.push.annotation.Action;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 */
@Aspect
@Configuration
public class ActionAop {

    private static final Logger logger = LoggerFactory.getLogger(ActionAop.class);

    @Autowired
    private ActionContext actionContext;


    /**
     * @param joinPoint
     * @param action
     * @return
     * @throws Throwable
     */
    @Around(value = "@annotation(action)")
    public Object action(ProceedingJoinPoint joinPoint, Action action) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String name = method.toString();

        ActionInfo info = actionContext.getActionInfos().get(name);

        try {
            if (null != info && info.getOnline() == 0) {
                logger.info("action {} offline", name);
                return null;
            }
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            throw throwable;
        }
    }

}
