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

import com.xiaomi.youpin.gwdash.bo.SwitchBo;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.ReleaseConfigBo;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.ReleaseConfigService;
import com.xiaomi.youpin.gwdash.service.SwitchService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Configuration
public class PipelineAspect {

    private static final Logger logger = LoggerFactory.getLogger(AopAspect.class);

    @Autowired
    SwitchService switchService;

    @Autowired
    private ReleaseConfigService releaseConfigService;

    /**
     * 拦截所有controller
     * 统计处理时长
     *
     * @param joinPoint
     * @return
     */
    @Around(value = "execution(* com.xiaomi.youpin.gwdash.controller.PipelineController.start*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            long projectId = Long.valueOf(request.getParameter("projectId"));
            ReleaseConfigBo appConfigBo = releaseConfigService.getConfig(1, projectId);
            SwitchBo switchBo = switchService.getConfig();
            boolean isAllow = (null == switchBo || switchBo.isRelease())
                    || (null == appConfigBo || appConfigBo.getCount() > 0);
            if (isAllow) {
                Object result = joinPoint.proceed();
                return result;
            }
            return new Result<Object>(1, "暂停发版",null);
        } catch (Throwable throwable) {
            logger.error("[PipelineAspect] error:", throwable);
            Result<String> result = new Result<>(CommonError.UnknownError.code, CommonError.UnknownError.message, throwable.getMessage());
            return result;
        }
    }
}
