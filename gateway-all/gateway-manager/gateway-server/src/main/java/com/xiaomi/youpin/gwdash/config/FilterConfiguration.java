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
