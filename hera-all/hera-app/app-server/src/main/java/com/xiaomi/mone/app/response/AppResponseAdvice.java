package com.xiaomi.mone.app.response;

import com.google.gson.Gson;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.response.ano.OriginalResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 13:36
 */
@ControllerAdvice
public class AppResponseAdvice implements ResponseBodyAdvice<Object> {

    private static Gson gson = new Gson();

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        if (ErrorController.class.isAssignableFrom(methodParameter.getExecutable().getDeclaringClass())) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        /**
         * return original response by this annotation
         */
        if (methodParameter.hasMethodAnnotation(OriginalResponse.class)) {
            return body;
        }
        if (body instanceof Result) {
            return body;
        } else if (body instanceof String) {
            try {
                return gson.toJson(Result.success(body));
            } catch (Exception e) {
                e.printStackTrace();
                return Result.error(e.getMessage());
            }
        }
        return Result.success(body);
    }
}
