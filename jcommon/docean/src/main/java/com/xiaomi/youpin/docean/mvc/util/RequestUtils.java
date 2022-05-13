package com.xiaomi.youpin.docean.mvc.util;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.mvc.HttpRequestUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2022/4/9 12:03
 */
public abstract class RequestUtils {

    private static Gson gson = new Gson();

    public static byte[] getData(FullHttpRequest request, Consumer consumer) {
        if (request.method().equals(HttpMethod.GET)) {
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            Map<String, String> params = decoder.parameters().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().get(0)));
            consumer.accept(params);
            return gson.toJson(params).getBytes();
        }
        if (request.method().equals(HttpMethod.POST)) {
            return HttpRequestUtils.getRequestBody(request);
        }
        return new byte[]{};
    }

    public static Map<String, String> headers(FullHttpRequest request) {
        return request.headers().entries().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
