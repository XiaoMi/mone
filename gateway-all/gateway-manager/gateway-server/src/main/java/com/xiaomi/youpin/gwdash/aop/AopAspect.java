/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.spring.PropertyPreFilters;
import com.google.common.collect.Sets;
import com.xiaomi.youpin.gwdash.annotation.OperationLog;
import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.bo.openApi.OperationLogRequest;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.OperationLogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.xiaomi.youpin.gwdash.common.Consts.SKIP_MI_DUN_USER_NAME;

@Aspect
@Configuration
public class AopAspect {

    private static final Logger logger = LoggerFactory.getLogger(AopAspect.class);

    @Autowired
    private LoginService loginService;

    @Autowired
    private OperationLogService operationLogService;

    @Value("${app.name:gwdash}")
    private String applicationName;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private ExecutorService executorService;

    /**
     * 拦截所有controller
     * 统计处理时长
     *
     * @param joinPoint
     * @return
     */
    @Around(value = "execution(* com.xiaomi.youpin.gwdash.controller.*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        StopWatch stopWatch = new StopWatch();
        Object[] o = joinPoint.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SessionAccount account = loginService.getAccountFromSession(request);
        String url = request.getRequestURL().toString();
        String method = request.getMethod();

        // PrivateApiController uses skip midun for tian-gong integration
        logger.info("[AopAspect] record info -- account: {}, url: {}, method: {}, params:{}",
                null == account ? "account is null, username: " +
                        Optional.ofNullable(request.getHeader(SKIP_MI_DUN_USER_NAME)).orElse("") :
                        account.getUsername(), url, method, Arrays.toString(o));

        try {
            stopWatch.start();
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("[AopAspect] error:" + url, throwable);
            CommonError errorType = CommonError.UnknownError;

            if (throwable instanceof IllegalArgumentException || throwable instanceof MethodArgumentNotValidException) {
                errorType = CommonError.InvalidParamError;
            }
            return new Result<>(errorType.getCode(), errorType.getMessage(), throwable.getMessage());
        } finally {
            stopWatch.stop();
            logger.info("[AopAspect] taking up time -- url: {}, time: {}", url, stopWatch.getTotalTimeMillis());
        }
    }

    @Around(value = "@annotation(com.xiaomi.youpin.gwdash.annotation.OperationLog)")
    public Object aroundSaveLog(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            Object result = joinPoint.proceed();
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            SessionAccount account = loginService.getAccountFromSession(request);
            String requestUrl = request.getRequestURL().toString();
            executorService.execute(() -> {
                operationLogService.saveOperationLog(buildOperationLog(args, result, joinPoint, account == null ? "" : account.getUsername(), requestUrl));
            });
            return result;
        } catch (Throwable throwable) {
            logger.error("annotation(com.xiaomi.youpin.gwdash.annotation.OperationLog)", throwable);
            return new Result<>(CommonError.UnknownError.getCode(), CommonError.UnknownError.getMessage(), throwable.getMessage());
        }

    }

    private OperationLogRequest buildOperationLog(Object[] args, Object result, JoinPoint joinPoint, String userName, String requestURL) {
        OperationLogRequest operationLog = new OperationLogRequest();
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            return null;
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (!method.isAnnotationPresent(OperationLog.class)) {
            return null;
        }
        OperationLog annotation = method.getAnnotation(OperationLog.class);
        OperationLog.LogType type = annotation.type();
        operationLog.setType(type.getCode());
        HashSet<OperationLog.Column> exclusionColumns = Sets.newHashSet(annotation.exclusion());
        if (!exclusionColumns.contains(OperationLog.Column.ARGS)) {
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof HttpServletRequest) {
                        args[i] = HttpServletRequest.class.getName();
                    }
                    if (args[i] instanceof HttpServletResponse) {
                        args[i] = HttpServletResponse.class.getName();
                    }
                }
            }
            String argsJsonString = JSON.toJSONString(args);
            if (argsJsonString.length() > 950) { // 太长了就不存了
                argsJsonString = "";
            }
            operationLog.setDataBefore(argsJsonString);
        } else {
            operationLog.setDataBefore("");
        }
        if (!exclusionColumns.contains(OperationLog.Column.RESULT)) {
            if (exclusionColumns.contains(OperationLog.Column.DATA)) {
                if (result instanceof Result) {
                    operationLog.setDataAfter(JSON.toJSONString(result, new PropertyPreFilters().addFilter().addExcludes("data")));
                }
            } else {
                operationLog.setDataAfter(JSON.toJSONString(result));
            }
        } else {
            operationLog.setDataAfter("");
        }
        operationLog.setUserName(userName);
        operationLog.setAppName(applicationName);
        operationLog.setDataId(requestURL);
        operationLog.setRemark("");
        return operationLog;
    }
}
