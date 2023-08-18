package com.xiaomi.data.push.client;

import com.google.common.collect.Maps;
import com.xiaomi.data.push.client.bo.HttpResult;
import com.xiaomi.data.push.client.common.PrintingEventListener;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2023/8/18 09:03
 */
@Slf4j
public class MoneHttpClient {


    private ConnectionPool pool = null;

    private OkHttpClient client = null;

    private int poolNum = 20;

    @Getter
    private ConcurrentHashMap<String, ConnectionPool> poolMap = new ConcurrentHashMap<>();


    public MoneHttpClient(int maxIdleConnections, int timeout, int poolNum) {
        this.poolNum = poolNum;
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


    public static BiFunction<Response, Call, HttpResult> getResFun = (res, call) -> {
        byte[] data = new byte[]{};
        try {
            data = res.body().source().readByteArray();
        } catch (IOException e) {
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
        result.setCall(call);
        return result;
    };


    @SneakyThrows
    public HttpResult get(String group, String url, Map<String, String> headers, int timeout) {
        Call call = getCall(group, "get", url, headers, null, timeout);
        return call(call, getResFun);
    }

    @SneakyThrows
    public HttpResult post(String group, String url, Map<String, String> headers, byte[] data, int timeout) {
        Call call = getCall(group, "post", url, headers, data, timeout);
        return call(call, getResFun);
    }

    public Call getCall(String group, String method, String url, Map<String, String> headers, byte[] data, int timeout) {
        ConnectionPool groupPool = poolMap.computeIfAbsent(String.valueOf(group.hashCode() % poolNum), (k) -> new ConnectionPool(20, 5, TimeUnit.MINUTES));
        OkHttpClient client = this.client.newBuilder().connectionPool(groupPool).callTimeout(timeout, TimeUnit.MILLISECONDS).build();
        Headers.Builder headersBuilder = new Headers.Builder();
        headers.forEach((k, v) -> headersBuilder.add(k, v));
        Request.Builder builder = new Request.Builder().url(url).headers(headersBuilder.build());
        if (method.equals("post")) {
            RequestBody requestBody = RequestBody.create(data);
            builder.post(requestBody);
        }
        Request req = builder.build();
        Call call = client.newCall(req);
        return call;
    }


    @SneakyThrows
    public HttpResult call(Call call, BiFunction<Response, Call, HttpResult> function) {
        try (Response res = call.execute()) {
            return function.apply(res, call);
        }
    }


}
