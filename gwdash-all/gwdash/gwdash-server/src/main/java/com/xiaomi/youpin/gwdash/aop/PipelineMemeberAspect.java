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
import com.xiaomi.youpin.gwdash.service.LoginService;
import com.xiaomi.youpin.gwdash.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.nutz.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Configuration
public class PipelineMemeberAspect {

    @Autowired
    private Dao dao;

    @Autowired
    private LoginService loginService;

    @Autowired
    private ProjectService projectService;

    /**
     * @param joinPoint
     * @return
     */
    @Around(value = "execution(* com.xiaomi.youpin.gwdash.controller.PipelineController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        SessionAccount account = loginService.getAccountFromSession(request);

        Object result = null;
        try {
            if (projectService.isProjectSuperUser(account) || projectService.isMember((Long) args[0], account)) {
                result = joinPoint.proceed();
            } else {
                result = new Result<>(1, "项目成员才可操作", null);
            }
        } catch (Throwable throwable) {
            log.error("PipelineMemeberAspect#around Throwable:" + throwable.getMessage(), throwable);
            result = new Result<>(1, throwable.getMessage(), null);
        }

        return result;
    }
}
