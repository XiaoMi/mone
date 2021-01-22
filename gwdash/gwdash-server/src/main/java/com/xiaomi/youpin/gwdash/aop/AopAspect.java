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

import com.xiaomi.youpin.gwdash.bo.SessionAccount;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.LoginService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;


@Aspect
@Configuration
public class AopAspect {

    private static final Logger logger = LoggerFactory.getLogger(AopAspect.class);

    @Autowired
    private LoginService loginService;

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

        logger.info("[AopAspect] record info -- account: {}, url: {}, method: {}, params:{}",
                null == account ? "account is null" : account.getUsername(), url, method, Arrays.toString(o));

        try {
            stopWatch.start();
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable throwable) {
            logger.error("[AopAspect] error:" + url, throwable);
            Result<String> result = new Result<>(CommonError.UnknownError.code, CommonError.UnknownError.message, throwable.getMessage());
            return result;
        } finally {
            stopWatch.stop();
            logger.info("[AopAspect] taking up time -- url: {}, time: {}", url, stopWatch.getTotalTimeMillis());
        }
    }
}
