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

package com.xiaomi.mone.tpc.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.aop.ArgCheck;
import com.xiaomi.mone.tpc.login.filter.HttpReqUserFilter;
import com.xiaomi.mone.tpc.login.filter.RpcReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
public class FilterConfiguration {

    @Value("${aegis.sdk.tpc.public.key}")
    private String aegisSdkTpc;
    @NacosValue("${token.parse.url:null}")
    private String tokenParseUrl;
    @Value("${dev.mode}")
    private String devMode;
    @Value("${inner.auth}")
    private String innerAuth;
    @NacosValue("${login.url:null}")
    private String loginUrl;
    @NacosValue("${logout.url:null}")
    private String logoutUrl;
    @NacosValue("${service.token.parse:null}")
    private String serviceTokenParse;
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public RpcReqUserFilter rpcReqUserFilter() {
        RpcReqUserFilter filter = new RpcReqUserFilter(serviceTokenParse, Result.fail(GeneralCodes.NotAuthorized, "请求认证不通过"));
        List<String> sysNames = new ArrayList<>();
        sysNames.add("mife");
        sysNames.add("hera-cloud");
        filter.setSupportSysNames(sysNames);
        return filter;
    }

    /**
     * 登陆态拦截
     * @return
     */
    @Bean
    public FilterRegistrationBean filterLoginBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpReqUserFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter(ConstUtil.devMode, devMode);
        registrationBean.addInitParameter(ConstUtil.innerAuth, innerAuth);
        registrationBean.addInitParameter(ConstUtil.CAS_PUBLIC_KEY, aegisSdkTpc);
        registrationBean.addInitParameter(ConstUtil.authTokenUrl, tokenParseUrl);
        registrationBean.addInitParameter(ConstUtil.loginUrl,loginUrl);
        registrationBean.addInitParameter(ConstUtil.logoutUrl,logoutUrl);
        String ignoreUrls = getIgnoreUrls();
        log.info("login ignore urls={}", ignoreUrls);
        if (StringUtils.isNotBlank(ignoreUrls)) {
            registrationBean.addInitParameter(ConstUtil.ignoreUrl, getIgnoreUrls());
        }
        registrationBean.setOrder(1);
        return registrationBean;
    }

    /**
     * 获取忽略路径
     * @return
     */
    private String getIgnoreUrls() {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(RequestMapping.class);
        if (CollectionUtils.isEmpty(beanMap)) {
            return null;
        }
        List<String> paths = new ArrayList<>();
        beanMap.values().stream().forEach(bean -> {
            Class<?> beanCls = bean.getClass().getSuperclass();
            Method[] methods = beanCls.getMethods();
            if (methods == null || methods.length == 0) {
                return;
            }
            RequestMapping beanMapping = beanCls.getAnnotation(RequestMapping.class);
            Arrays.stream(methods).forEach(method -> {
                RequestMapping methodMapping = method.getAnnotation(RequestMapping.class);
                if (methodMapping == null || methodMapping.value() == null
                        || methodMapping.value().length == 0) {
                    return;
                }
                ArgCheck argCheck = method.getAnnotation(ArgCheck.class);
                if (argCheck == null || argCheck.needUser()) {
                    return;
                }
                if (beanMapping != null && beanMapping.value() != null && beanMapping.value().length != 0) {
                    Arrays.stream(beanMapping.value()).forEach(beanPath -> {
                        Arrays.stream(methodMapping.value()).forEach(methPath -> {
                            paths.add(beanPath + methPath);
                        });
                    });
                } else {
                    Arrays.stream(methodMapping.value()).forEach(methPath -> {
                        paths.add(methPath);
                    });
                }
            });
        });
        if (paths.isEmpty()) {
            return null;
        }
        return StringUtils.join(paths, ",");
    }

}
