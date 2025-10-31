package run.mone.agentx.config;

import com.xiaomi.mone.tpc.login.filter.HttpReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean
    @ConditionalOnProperty(name = "auth.filter.enabled", havingValue = "true")
    public FilterRegistrationBean filterCasBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
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

}
