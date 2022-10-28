///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin.gwdash.config;
//
//import com.xiaomi.aegis.filter.AegisFilter;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FilterConfiguration {
//
//    @Value("${server.casServerLoginUrl}")
//    private String casServerLoginUrl;
//
//    @Value("${server.casServerUrlPrefix}")
//    private String casServerUrlPrefix;
//
//    @Value("${server.serverName}")
//    private String serverName;
//
//    @Value("${aegis.sdk.public.key}")
//    private String aegisSdk;
//
//    @Value("${aegis.sdk.public.mone.key}")
//    private String aegisMoneSdk;
//
//    @Value("${server.cas.ignoreUrl}")
//    private String ignoreUrl;
//
//    @Bean
//    public FilterRegistrationBean filterCasBean() {
//        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
//        registrationBean.setFilter(new AegisFilter());
//        registrationBean.addUrlPatterns("/api/*");
//        registrationBean.addInitParameter("AEGIS_SDK_PUBLIC_KEY", aegisSdk + "," + aegisMoneSdk);
//        registrationBean.addInitParameter("IGNORE_URL", ignoreUrl);
//        registrationBean.setOrder(4);
//        return registrationBean;
//    }
//
//}
