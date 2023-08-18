package com.xiaomi.data.push.client;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.bo.HttpResult;
import com.xiaomi.data.push.client.common.PrintingEventListener;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2023/8/18 09:03
 */
@Slf4j
public class MoneHttpClient {


    private ConnectionPool pool = null;

    private OkHttpClient client = null;


    public MoneHttpClient(int maxIdleConnections, int timeout) {
        pool = new ConnectionPool(maxIdleConnections, 4, TimeUnit.MINUTES);
        client = new OkHttpClient.Builder()
                .connectionPool(pool)
                .eventListenerFactory(PrintingEventListener.FACTORY)
                .callTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    public String info() {
        return "connectionCount:" + pool.connectionCount() + ",idleConnectionCount:" + pool.idleConnectionCount();
    }


    private static Function<Response, HttpResult> getResFun = res -> {
        byte[] data = new byte[]{};
        if (res.body().contentLength() > 0) {
            try {
                data = res.body().source().readByteArray();
            } catch (IOException e) {
            }
        }
        HttpResult result = new HttpResult();
        result.setData(data);
        result.setCode(res.code());
        Map<String, String> h = Maps.newHashMap();
        res.headers().forEach(it -> {
            log.debug("{}->{}", it.getFirst(), it.getSecond());
            h.put(it.getFirst(), it.getSecond());
        });
        result.setRespHeaders(h);
        return result;
    };


    @SneakyThrows
    public HttpResult get(String url, Map<String, String> headers, int timeout) {
        return call("get", url, headers, null, timeout, MoneHttpClient.getResFun);
    }

    @SneakyThrows
    public HttpResult post(String url, Map<String, String> headers, byte[] data, int timeout) {
        return call("post", url, headers, data, timeout, MoneHttpClient.getResFun);
    }


    @SneakyThrows
    private HttpResult call(String method, String url, Map<String, String> headers, byte[] data, int timeout, Function<Response, HttpResult> function) {
        OkHttpClient client = this.client.newBuilder().callTimeout(timeout, TimeUnit.MILLISECONDS).build();
        Headers.Builder headersBuilder = new Headers.Builder();
        headers.forEach((k, v) -> headersBuilder.add(k, v));
        Request.Builder builder = new Request.Builder().url(url).headers(headersBuilder.build());
        if (method.equals("post")) {
            RequestBody requestBody = RequestBody.create(data);
            builder.post(requestBody);
        }
        Request req = builder.build();
        Call call = client.newCall(req);
        try (Response res = call.execute()) {
            res.headers().forEach(it -> {
                log.debug("{}->{}", it.getFirst(), it.getSecond());
            });
            return function.apply(res);
        }
    }


}
