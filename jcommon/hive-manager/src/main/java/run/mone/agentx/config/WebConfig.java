package run.mone.agentx.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/dist/assets/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/agent-manager").setViewName("forward:/index.html");
        registry.addViewController("/agent-manager/**").setViewName("forward:/index.html");
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        // 添加对text/event-stream的支持
        List<MediaType> mediaTypes = new ArrayList<>(converter.getSupportedMediaTypes());
        mediaTypes.add(MediaType.valueOf("text/event-stream"));
        mediaTypes.add(MediaType.valueOf("text/event-stream;charset=UTF-8"));
        converter.setSupportedMediaTypes(mediaTypes);
        return converter;
    }
} 