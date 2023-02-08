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

package com.xiaomi.miapi.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.mone.tpc.login.filter.HttpReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @NacosValue("${aegis.sdk.public.key}")
    private String aegisSdk;

    @Value("${aegis.sdk.mone.public.key}")
    private String aegisMoneSdk;

    @Value("${server.cas.ignoreUrl}")
    private String ignoreUrl;

    @Value("${inner.auth}")
    private String innerAuth;

    @Value("${token.parse.url}")
    private String tokenParseUrl;

    @Value("${dev.mode}")
    private String devMode;

    @Bean
    public FilterRegistrationBean filterCasBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpReqUserFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter(ConstUtil.innerAuth, innerAuth);
        registrationBean.addInitParameter(ConstUtil.CAS_PUBLIC_KEY, aegisSdk + "," + aegisMoneSdk);
        registrationBean.addInitParameter(ConstUtil.authTokenUrl, tokenParseUrl);
        registrationBean.addInitParameter("IGNORE_URL", ignoreUrl);
        registrationBean.addInitParameter(ConstUtil.devMode, devMode);
        registrationBean.addInitParameter(ConstUtil.USER_INFO_PATH, "/api/miapi/login/userinfo");
        registrationBean.setOrder(4);
        return registrationBean;
    }

}
