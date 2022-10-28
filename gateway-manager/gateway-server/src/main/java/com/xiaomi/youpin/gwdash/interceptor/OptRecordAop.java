//package com.xiaomi.youpin.gwdash.interceptor;
//
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.dao.model.OptRecord;
//import com.xiaomi.youpin.gwdash.service.OptRecordService;
//import com.xiaomi.youpin.gwdash.service.LoginService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Date;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * @author wm
// */
//@Aspect
//@Component
//@Slf4j
//public class OptRecordAop {
//
//    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(10);
//
//    @Autowired
//    OptRecordService optRecordService;
//    @Autowired
//    private LoginService loginService;
//
//    @Around("execution(* com.xiaomi.youpin.gwdash.controller.DevTestController.*(..))")
//    public Object around(ProceedingJoinPoint joinPoint) {
//        long startTime = System.currentTimeMillis();
//         OptRecord optRecord = new OptRecord();
//
//        Object result = null;
//        try {
//            beforeOptRecord(optRecord, joinPoint);
//            result = joinPoint.proceed();
//        } catch (Throwable throwable) {
//            throwable.printStackTrace();
//        } finally {
//            afterOptRecord(optRecord, result);
//            optRecord.setDuration((int)(System.currentTimeMillis() - startTime));
//            fixedThreadPool.execute(() -> optRecordService.newOptRecord(optRecord));
//        }
//        return result;
//    }
//
//    private OptRecord beforeOptRecord(OptRecord optRecord, ProceedingJoinPoint joinPoint) {
//        try {
//            ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
//            HttpServletRequest request = sra.getRequest();
//            String username = loginService.getAccountFromSession(request).getUsername();
//            optRecord.setOptId(StringUtils.isNotEmpty(username)? username:request.getParameter("userName"));
//            optRecord.setOptTime(new Date());
//            optRecord.setClientIp(request.getRemoteAddr());
//            optRecord.setResourceUrl(request.getRequestURI());
//            optRecord.setReqMethod(request.getMethod());
//            //TODO 需要改成更通用的获取入参方式
//            optRecord.setInParam(new Gson().toJson(joinPoint.getArgs()[0]));
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return optRecord;
//    }
//
//    private void afterOptRecord(OptRecord optRecord, Object result){
//        try {
//            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
//            optRecord.setReturnCode(response.getStatus()+"");
//            if (result!=null){
//                optRecord.setOutParam(new Gson().toJson(result));
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//    }
//
//}
