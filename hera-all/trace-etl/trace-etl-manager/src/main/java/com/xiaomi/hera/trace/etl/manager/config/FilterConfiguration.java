package com.xiaomi.hera.trace.etl.manager.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.hera.trace.etl.manager.filter.RequestHeaderFilter;
import com.xiaomi.mone.tpc.login.filter.HttpReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/5/30 8:06 下午
 */
@Configuration
public class FilterConfiguration {

    @NacosValue("${tpc.token.parse.url}")
    public String tokenParseUrl;

    @Bean
    public FilterRegistrationBean requestHeaderBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new RequestHeaderFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean filterCasBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpReqUserFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter(ConstUtil.innerAuth, "false");
        registrationBean.addInitParameter("IGNORE_URL", "/tracing/v1/*");
        registrationBean.addInitParameter(ConstUtil.authTokenUrl, tokenParseUrl);
        registrationBean.addInitParameter(ConstUtil.devMode,"false");

        registrationBean.setOrder(0);
        return registrationBean;
    }
}
