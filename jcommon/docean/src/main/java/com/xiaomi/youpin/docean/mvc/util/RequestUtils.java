package com.xiaomi.youpin.docean.mvc.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.xiaomi.youpin.docean.config.HttpServerConfig;
import com.xiaomi.youpin.docean.mvc.HttpRequestUtils;
import com.xiaomi.youpin.docean.mvc.upload.Upload;
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

    public static byte[] getData(HttpServerConfig config, String uri, FullHttpRequest request) {
        return getData(config, uri, request, null);
    }

    public static byte[] getData(HttpServerConfig config, String uri, FullHttpRequest request, Consumer consumer) {
        if (Upload.isUpload(uri)) {
            if (config.isUpload()) {
                QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                Map<String, String> params = decoder.parameters().entrySet().stream().collect(Collectors.toMap(it -> it.getKey(), it -> it.getValue().get(0)));
                consumer.accept(params);
                String fileName = Upload.upload(config.getUploadDir(), request);
                return fileName.getBytes();
            } else {
                throw new RuntimeException("don't support upload file");
            }
        }
        if (request.method().equals(HttpMethod.GET)) {
            return new byte[]{};
        }
        if (request.method().equals(HttpMethod.POST)) {
            return HttpRequestUtils.getRequestBody(request);
        }
        return new byte[]{};
    }

    public static Map<String, String> headers(FullHttpRequest request) {
        return request.headers().entries().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e, n) -> n));
    }

    public static JsonObject createSessionJsonObject(String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("__type__", "session");
        obj.addProperty("__name__", name);
        return obj;
    }

}
