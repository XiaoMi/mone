package run.mone.m78.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import run.mone.m78.server.config.auth.AuthRoleInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * @author caobaoyu
 * @description:
 * @date 2024-03-09 10:57
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthRoleInterceptor authRoleInterceptor;

    @Value("${server.cas.ignoreUrl}")
    private String ignoreUrl;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        ignoreUrl = ignoreUrl + ",/api/v1/chat/ask";
        String[] split = ignoreUrl.split(",");
        List<String> list = Arrays.stream(split).map(i -> i.endsWith("*") ? i + "*" : i).toList();
        registry.addInterceptor(authRoleInterceptor).excludePathPatterns(list).excludePathPatterns("/error");
    }

}
