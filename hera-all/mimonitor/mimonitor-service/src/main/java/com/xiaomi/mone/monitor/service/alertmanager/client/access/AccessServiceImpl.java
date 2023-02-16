package com.xiaomi.mone.monitor.service.alertmanager.client.access;

import com.xiaomi.mone.monitor.service.alertmanager.client.Request;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.HttpMethodName;
import com.xiaomi.mone.monitor.service.alertmanager.client.signer.Signer;
import org.apache.http.Header;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

/**
 * @author gaoxihui
 * @date 2022/11/10 2:50 下午
 */
public class AccessServiceImpl extends AccessService{
    private static final String UTF8 = "UTF-8";

    public AccessServiceImpl(String ak, String sk) {
        super(ak, sk);
    }

    public HttpRequestBase access(String url, Map<String, String> headers, String content, HttpMethodName httpMethod) throws Exception {
        Request request = new Request();
        request.setAppKey(this.ak);
        request.setAppSecrect(this.sk);
        request.setMethod(httpMethod.name());
        request.setUrl(url);
        Iterator headersIterator = headers.keySet().iterator();

        while(headersIterator.hasNext()) {
            String k = (String)headersIterator.next();
            request.addHeader(k, (String)headers.get(k));
        }

        request.setBody(content);
        Signer signer = new Signer();
        signer.sign(request);
        HttpRequestBase httpRequestBase = createRequest(url, (Header)null, content, httpMethod);
        Map<String, String> requestHeaders = request.getHeaders();
        Iterator requestHeadersIterator = requestHeaders.keySet().iterator();

        while(requestHeadersIterator.hasNext()) {
            String key = (String)requestHeadersIterator.next();
            if (!key.equalsIgnoreCase("Content-Length".toString())) {
                String value = (String)requestHeaders.get(key);
                httpRequestBase.addHeader(key, new String(value.getBytes("UTF-8"), "ISO-8859-1"));
            }
        }

        return httpRequestBase;
    }

    public HttpRequestBase access(String url, Map<String, String> headers, InputStream content, Long contentLength, HttpMethodName httpMethod) throws Exception {
        String body = "";
        if (content != null) {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];

            int length;
            while((length = content.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            body = result.toString("UTF-8");
        }

        return this.access(url, headers, body, httpMethod);
    }

    private static HttpRequestBase createRequest(String url, Header header, String content, HttpMethodName httpMethod) {
        Object httpRequest;
        StringEntity entity;
        if (httpMethod == HttpMethodName.POST) {
            HttpPost postMethod = new HttpPost(url);
            if (content != null) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                postMethod.setEntity(entity);
            }

            httpRequest = postMethod;
        } else if (httpMethod == HttpMethodName.PUT) {
            HttpPut putMethod = new HttpPut(url);
            httpRequest = putMethod;
            if (content != null) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                putMethod.setEntity(entity);
            }
        } else if (httpMethod == HttpMethodName.PATCH) {
            HttpPatch patchMethod = new HttpPatch(url);
            httpRequest = patchMethod;
            if (content != null) {
                entity = new StringEntity(content, StandardCharsets.UTF_8);
                patchMethod.setEntity(entity);
            }
        } else if (httpMethod == HttpMethodName.GET) {
            httpRequest = new HttpGet(url);
        } else if (httpMethod == HttpMethodName.DELETE) {
            httpRequest = new HttpDelete(url);
        } else if (httpMethod == HttpMethodName.OPTIONS) {
            httpRequest = new HttpOptions(url);
        } else {
            if (httpMethod != HttpMethodName.HEAD) {
                throw new RuntimeException("Unknown HTTP method name: " + httpMethod);
            }

            httpRequest = new HttpHead(url);
        }

        ((HttpRequestBase)httpRequest).addHeader(header);
        return (HttpRequestBase)httpRequest;
    }
}
