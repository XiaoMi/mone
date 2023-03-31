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

package com.xiaomi.mone.monitor.config;

import com.xiaomi.mone.tpc.login.filter.HttpReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FilterConfiguration {

    @NacosValue("${aegis.sdk.hera.public.key:noconfig}")
    private String aegisSdkHera;

    @NacosValue("${aegis.sdk.log.public.key:noconfig}")
    private String aegisSdkLog;

    @NacosValue("${aegis.mione.public.domain.key:noconfig}")
    private String mionePublicDomain;

    @NacosValue(value = "${token.parse.url}",autoRefreshed = true)
    private String tokenParseUrl;
    @Value("${inner.auth}")
    private String innerAuth;
    @Value("${dev.mode}")
    private String devMode;
    @NacosValue(value = "${login.url}",autoRefreshed = true)
    private String loginUrl;

    @Bean
    public FilterRegistrationBean filterCasBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpReqUserFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter(ConstUtil.innerAuth, innerAuth);
        String value = aegisSdkHera+","+aegisSdkLog + "," + mionePublicDomain;
        registrationBean.addInitParameter(ConstUtil.CAS_PUBLIC_KEY, value);
        registrationBean.addInitParameter(ConstUtil.authTokenUrl, tokenParseUrl);
        registrationBean.addInitParameter(ConstUtil.ignoreUrl, "js/*,/api/*,/alert/*,/manual/*,/prometheus/queryIncrease,/prometheus/detail,/prometheus/logInfo,/prometheus/getTeslaError,/alertGroup/test,/mimonitor/alarmUnHealthSendFeishu,/mimonitor/alarmResourceUtilization,/mimonitor/alarmUnHealthSendFeishu");
        registrationBean.addInitParameter(ConstUtil.devMode,devMode);
        registrationBean.addInitParameter(ConstUtil.loginUrl,loginUrl);
        registrationBean.setOrder(0);
        return registrationBean;
    }

}
