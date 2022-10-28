//package com.xiaomi.youpin.gwdash.interceptor;
//
//import com.xiaomi.aegis.utils.AegisFacade;
//import com.xiaomi.aegis.vo.UserInfoVO;
//import org.apache.dubbo.rpc.RpcContext;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestAttributes;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * @author gaoyibo
// */
//@Aspect
//@Component
//public class ScepterAop {
//    private String getUsername(HttpServletRequest request) {
//        UserInfoVO user = AegisFacade.getUserInfo(request);
//        if (null == user) {
//            return null;
//        }
//        return user.getUser();
//    }
//
//    @Around("execution(* com.xiaomi.youpin.gwdash.controller.ScepterController.*(..))")
//    public Object setUsername(ProceedingJoinPoint joinPoint) {
//        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//        HttpServletRequest request = sra.getRequest();
//
//        Object result = null;
//
//        try {
//            String username = getUsername(request);
//            RpcContext.getContext().setAttachment("username", username);
//            result = joinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        }
//
//        return result;
//    }
//}
