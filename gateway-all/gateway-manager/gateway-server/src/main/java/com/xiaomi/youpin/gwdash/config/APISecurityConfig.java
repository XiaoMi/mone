package com.xiaomi.youpin.gwdash.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.youpin.gwdash.filter.APIKeyAuthFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @author Xirui Yang (yangxirui)
 * @version 1.0
 * @since 2022/3/1
 */
@Slf4j
@Configuration
@EnableWebSecurity
@Order(1)
public class APISecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${server.http.auth-token-header-name}")
    private String principalRequestHeader;

    @Value("${server.http.auth-token}")
    private String principalRequestValue;

    /**
     * 是否开放PrivateApiController
     */
    @NacosValue(value = "${private.api.disable:false}", autoRefreshed = true)
    private boolean disablePrivateApi;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        log.info("loading APISecurityConfig, config private.api.disable is {}", this.disablePrivateApi);
        APIKeyAuthFilter filter = new APIKeyAuthFilter(principalRequestHeader);

        filter.setAuthenticationManager(authentication -> {
            if (this.disablePrivateApi) {
                throw new DisabledException("private api temporarily disabled by config");
            }
            String principal = (String) authentication.getPrincipal();
            if (!principalRequestValue.equals(principal)) {
                throw new BadCredentialsException("The API key was not found or not the expected value.");
            }
            authentication.setAuthenticated(true);
            return authentication;
        });
        httpSecurity.
                antMatcher("/open/v1/private/api/**").
                csrf().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).
                and().addFilter(filter).authorizeRequests().anyRequest().authenticated();
    }
}