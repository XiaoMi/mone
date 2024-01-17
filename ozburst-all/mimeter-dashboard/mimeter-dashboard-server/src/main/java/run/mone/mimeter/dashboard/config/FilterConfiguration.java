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

package run.mone.mimeter.dashboard.config;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.xiaomi.aegis.filter.AegisFilter;
import com.xiaomi.mone.tpc.login.filter.HttpReqUserFilter;
import com.xiaomi.mone.tpc.login.util.ConstUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfiguration {

    @NacosValue("${aegis.sdk.public.key:}")
    private String aegisSdk;

    @Value("${server.cas.ignoreUrl}")
    private String ignoreUrl;

    @Value("${is_local:false}")
    private String isLocal;

    @Value("${cas.inner.auth}")
    private String innerAuth;

    @NacosValue(value = "${token.parse.url:}", autoRefreshed = true)
    private String tokenParseUrl;

    @NacosValue(value = "${server.cas.loginUrl:}", autoRefreshed = true)
    private String loginUrl;

    @NacosValue(value = "${server.cas.logoutUrl:}", autoRefreshed = true)
    private String logoutUrl;

    @NacosValue(value = "${server.cas.userInfoPath:}", autoRefreshed = true)
    private String userInfoPath;

    @Bean
    public FilterRegistrationBean filterCasBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new HttpReqUserFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter(ConstUtil.innerAuth, innerAuth);
        registrationBean.addInitParameter(ConstUtil.authTokenUrl, tokenParseUrl);
        registrationBean.addInitParameter(ConstUtil.CAS_PUBLIC_KEY, aegisSdk);
        if (isLocal.equals("true")) {
            ignoreUrl = "/*";
        }
        registrationBean.addInitParameter("IGNORE_URL", ignoreUrl);
        registrationBean.addInitParameter("devMode", "false");
        registrationBean.addInitParameter(ConstUtil.USER_INFO_PATH, userInfoPath);
        registrationBean.addInitParameter(ConstUtil.loginUrl, loginUrl);
        registrationBean.addInitParameter(ConstUtil.logoutUrl, logoutUrl);
        registrationBean.setOrder(0);
        return registrationBean;
    }


}
