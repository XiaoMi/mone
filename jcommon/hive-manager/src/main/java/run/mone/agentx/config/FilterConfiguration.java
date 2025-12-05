package run.mone.agentx.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Filter configuration
 * 
 * Note: The HttpReqUserFilter from mi-tpc-login uses javax.servlet.Filter which is incompatible
 * with Spring Boot 3.x (jakarta.servlet.Filter). The mi-tpc-login dependency needs to be upgraded
 * to support Jakarta EE 9+ before this filter can be re-enabled.
 * 
 * To re-enable, set auth.filter.enabled=true in application.yml and ensure mi-tpc-login
 * is upgraded to a Jakarta-compatible version.
 */
@Configuration
public class FilterConfiguration {

    @Value("${auth.aegis.sdk.public.key}")
    private String aegisSdk;

    @Value("${server.cas.ignoreUrl}")
    private String ignoreUrl;

    @Value("${auth.token.parse.url}")
    private String tokenParseUrl;

    @Value("${auth.cas.inner.auth}")
    private String innerAuth;

    // Commented out due to javax.servlet vs jakarta.servlet incompatibility
    // Uncomment and update when mi-tpc-login is upgraded to Jakarta EE 9+
    /*
    @Bean
    @ConditionalOnProperty(name = "auth.filter.enabled", havingValue = "true")
    public FilterRegistrationBean<HttpReqUserFilter> filterCasBean() {
        FilterRegistrationBean<HttpReqUserFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new HttpReqUserFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.addInitParameter(ConstUtil.innerAuth, innerAuth);
        registrationBean.addInitParameter(ConstUtil.CAS_PUBLIC_KEY, aegisSdk);
        registrationBean.addInitParameter(ConstUtil.authTokenUrl, tokenParseUrl);
        registrationBean.addInitParameter("IGNORE_URL", ignoreUrl);
        registrationBean.addInitParameter("devMode", "false");
        registrationBean.setOrder(5);
        return registrationBean;
    }
    */

}
