package com.xiaomi.mone.monitor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * @author gaoxihui
 * @date 2021/7/22 9:34 上午
 */
@Configuration
public class RestTemplateConfig {

    @Value("${resttemplate.connection.timeout}")
    private int restTemplateConnectionTimeout;
    @Value("${resttemplate.read.timeout}")
    private int restTemplateReadTimeout;

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory simleClientHttpRequestFactory) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(simleClientHttpRequestFactory);
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory reqFactory= new SimpleClientHttpRequestFactory();
        reqFactory.setConnectTimeout(restTemplateConnectionTimeout);
        reqFactory.setReadTimeout(restTemplateReadTimeout);
        return reqFactory;
    }
}
