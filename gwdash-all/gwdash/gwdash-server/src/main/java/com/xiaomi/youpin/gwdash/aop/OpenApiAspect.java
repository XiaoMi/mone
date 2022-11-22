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

import com.xiaomi.youpin.gwdash.bo.openApi.RequestParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.service.UserService;
import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.request.QueryRoleRequest;
import com.xiaomi.youpin.hermes.bo.response.Account;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.xiaomi.youpin.gwdash.exception.CommonError.*;

@Slf4j
@Aspect
@Configuration
public class OpenApiAspect {

    @Autowired
    private UserService userService;

    @Value("${hermes.project.name}")
    private String projectName;

    /**
     * @param joinPoint   OpenApiController aop
     * @return
     */
    @Around(value = "execution(* com.xiaomi.youpin.gwdash.controller.OpenApiController.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String url = request.getRequestURL().toString();
        String method = request.getMethod();

        Object obj = Arrays.stream(joinPoint.getArgs()).filter(e -> e instanceof RequestParam).findAny().orElse(null);
        if (obj == null) {
            return Result.fail(InvalidParamError);
        }
        RequestParam param = (RequestParam) obj;
        if (StringUtils.isBlank(param.getUserName()) || StringUtils.isBlank(param.getToken())) {
            return Result.fail(InvalidParamError);
        }
        log.info("[OpenApiAspect] url: {}, method: {}, username:{}", url, method, param.getUserName());
        Account account = userService.queryUserByName(param.getUserName());
        if (null == account) {
            log.warn("account is null name:{}", param.getUserName());
            return Result.fail(AccountNotFoundError);
        }
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            return Result.fail(AccountNotFoundError);
        }

        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(param.getUserName());
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        if (CollectionUtils.isEmpty(roles) && roles.parallelStream().filter(e -> e.getName().contains("sre")).findAny().orElse(null) == null) {
            log.error("only sre can use open api, username: {}", param.getUserName());
            return Result.fail(UnAuthorized);
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("OpenApiAspect#around Throwable:" + throwable.getMessage(), throwable);
            result = new Result<>(1, throwable.getMessage(), null);
        }

        return result;
    }


    /**
     * @param joinPoint   OpenApiServiceImpl aop
     * @return
     */
    @Around(value = "execution(* com.xiaomi.youpin.gwdash.service.OpenApiServiceImpl.*(..)) && !execution(* com.xiaomi.youpin.gwdash.service.OpenApiServiceImpl.getConfigs(..))")
    public Object around2(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        Object obj = Arrays.stream(joinPoint.getArgs()).filter(e -> e instanceof RequestParam).findAny().orElse(null);
        if (obj == null) {
            return com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        RequestParam param = (RequestParam) obj;
        if (StringUtils.isBlank(param.getUserName()) || StringUtils.isBlank(param.getToken())) {
            return com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.ParamError, "参数错误");
        }
        Account account = userService.queryUserByName(param.getUserName());
        if (null == account) {
            log.warn("account is null name:{}", param.getUserName());
            return com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.NotAuthorized, "未授权");
        }
        if (account == null || StringUtils.isEmpty(account.getToken()) || !Objects.equals(account.getToken(), param.getToken())) {
            return com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.ParamError, "参数错误");
        }

        QueryRoleRequest queryRoleRequest = new QueryRoleRequest();
        queryRoleRequest.setProjectName(projectName);
        queryRoleRequest.setUserName(param.getUserName());
        List<RoleBo> roles = userService.getRoleByProjectName(queryRoleRequest);
        if (CollectionUtils.isEmpty(roles) && roles.parallelStream().filter(e -> e.getName().contains("sre")).findAny().orElse(null) == null) {
            log.error("only sre can use open api, username: {}", param.getUserName());
            return com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.Forbidden, "权限不足");
        }

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            log.error("OpenApiAspect#around Throwable:" + throwable.getMessage(), throwable);
            result = com.xiaomi.youpin.infra.rpc.Result.fail(GeneralCodes.InternalError, "系统内部错误");
        }

        return result;
    }
}
