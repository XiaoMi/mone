package com.xiaomi.youpin.dubbo.request;

import java.io.Serializable;
import java.util.Map;


/**
 * @author goodjava@qq.com
 * rpc 上下文,可用来透传数据
 */
public class RequestContext implements Serializable {
    public static final String UID = "uid";
    public static final String HEADERS = "headers";
    public static final String TOKEN = "token";
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String ATTACHMENT = "attachment";

    private String uid;
    private String token;
    private String traceId;
    private String spanId;
    private Map<String, String> attachment;
    private Map<String, String> headers;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
