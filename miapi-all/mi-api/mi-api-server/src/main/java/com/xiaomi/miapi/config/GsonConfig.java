package com.xiaomi.miapi.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Configuration
public class GsonConfig {
    @Bean
    GsonHttpMessageConverter gsonHttpMessageConverter() {
        GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
        GsonBuilder builder = new GsonBuilder();
        builder.serializeNulls();
        Gson gson = builder.create();
        converter.setGson(gson);
        return converter;
    }
}
