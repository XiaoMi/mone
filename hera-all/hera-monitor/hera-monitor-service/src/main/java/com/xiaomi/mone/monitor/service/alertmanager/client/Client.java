package com.xiaomi.mone.monitor.service.alertmanager.client;

import com.xiaomi.mone.monitor.service.alertmanager.client.access.AccessService;
import com.xiaomi.mone.monitor.service.alertmanager.client.access.AccessServiceImpl;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import org.apache.http.client.methods.HttpRequestBase;

import java.util.Map;

/**
 * @author gaoxihui
 * @date 2022/11/10 2:39 下午
 */
public class Client {
    public Client() {
    }

    public static HttpRequestBase sign(Request request) throws Exception {
        String appKey = request.getKey();
        String appSecrect = request.getSecrect();
        String url = request.getUrl();
        String body = request.getBody();
        Map<String, String> headers = request.getHeaders();
        switch(request.getMethod()) {
            case GET:
                return get(appKey, appSecrect, url, headers);
            case POST:
                return post(appKey, appSecrect, url, headers, body);
            case PUT:
                return put(appKey, appSecrect, url, headers, body);
            case PATCH:
                return patch(appKey, appSecrect, url, headers, body);
            case DELETE:
                return delete(appKey, appSecrect, url, headers);
            case HEAD:
                return head(appKey, appSecrect, url, headers);
            case OPTIONS:
                return options(appKey, appSecrect, url, headers);
            default:
                throw new IllegalArgumentException(String.format("unsupported method:%s", request.getMethod().name()));
        }
    }

    public static HttpRequestBase put(String ak, String sk, String requestUrl, Map<String, String> headers, String putBody) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        HttpMethodName httpMethod = HttpMethodName.PUT;
        if (putBody == null) {
            putBody = "";
        }

        HttpRequestBase request = accessService.access(requestUrl, headers, putBody, httpMethod);
        return request;
    }

    public static HttpRequestBase patch(String ak, String sk, String requestUrl, Map<String, String> headers, String body) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        HttpMethodName httpMethod = HttpMethodName.PATCH;
        if (body == null) {
            body = "";
        }

        HttpRequestBase request = accessService.access(requestUrl, headers, body, httpMethod);
        return request;
    }

    public static HttpRequestBase delete(String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        HttpMethodName httpMethod = HttpMethodName.DELETE;
        HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
        return request;
    }

    public static HttpRequestBase get(String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        HttpMethodName httpMethod = HttpMethodName.GET;
        HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
        return request;
    }

    public static HttpRequestBase post(String ak, String sk, String requestUrl, Map<String, String> headers, String postbody) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        if (postbody == null) {
            postbody = "";
        }

        HttpMethodName httpMethod = HttpMethodName.POST;
        HttpRequestBase request = accessService.access(requestUrl, headers, postbody, httpMethod);
        return request;
    }

    public static HttpRequestBase head(String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        HttpMethodName httpMethod = HttpMethodName.HEAD;
        HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
        return request;
    }

    public static HttpRequestBase options(String ak, String sk, String requestUrl, Map<String, String> headers) throws Exception {
        AccessService accessService = new AccessServiceImpl(ak, sk);
        HttpMethodName httpMethod = HttpMethodName.OPTIONS;
        HttpRequestBase request = accessService.access(requestUrl, headers, httpMethod);
        return request;
    }

}
