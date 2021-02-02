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

package com.xiaomi.youpin.gwdash.config;

import com.xiaomi.youpin.hermes.filter.HermesFilter;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.validation.Cas20ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class FilterConfiguration {

    @Value("${server.casServerLoginUrl}")
    private String casServerLoginUrl;

    @Value("${server.casServerUrlPrefix}")
    private String casServerUrlPrefix;

    @Value("${server.serverName}")
    private String serverName;

    /**
     * interceptor for CAS authentication
     *
     * @return
     */
    @Bean
    public FilterRegistrationBean<AuthenticationFilter> casAuthFilterRegBean() {
        FilterRegistrationBean<AuthenticationFilter> registration =
                new FilterRegistrationBean<>();
        registration.setFilter(new AuthenticationFilter());

        Map<String, String> initParam = new HashMap<>();
        initParam.put("casServerLoginUrl", casServerLoginUrl);
        initParam.put("serverName", serverName);
        initParam.put("authenticationRedirectStrategyClass", "com.xiaomi.youpin.gwdash.config.ErrCodeAuthenticationRedirectStrategy");
        registration.setInitParameters(initParam);

        List<String> urlPatterns = Arrays.asList("/gwdash/*", "/api/*", "/ws/*");
        registration.setUrlPatterns(urlPatterns);
        registration.setOrder(3);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<Cas20ProxyReceivingTicketValidationFilter>
    casValidFilterRegBean() {
        FilterRegistrationBean<Cas20ProxyReceivingTicketValidationFilter>
                registration = new FilterRegistrationBean<>();
        registration.setFilter(new Cas20ProxyReceivingTicketValidationFilter());

        Map<String, String> initParam = new HashMap<>();
        initParam.put("casServerUrlPrefix", casServerUrlPrefix);
        initParam.put("serverName", serverName);
        initParam.put("redirectAfterValidation", "true");
        registration.setInitParameters(initParam);

        List<String> urlPatterns = Arrays.asList("/gwdash/*", "/api/*");
        registration.setUrlPatterns(urlPatterns);
        registration.setOrder(4);

        return registration;
    }

    @Bean
    public FilterRegistrationBean<HermesFilter> hermes() {
        FilterRegistrationBean<HermesFilter>
                registration = new FilterRegistrationBean<>();
        registration.setFilter(new HermesFilter());

        List<String> patterns = Arrays.asList("/api/*");
        registration.setUrlPatterns(patterns);
        registration.setOrder(4);
        return registration;
    }
}
