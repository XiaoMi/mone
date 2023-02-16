package com.xiaomi.hera.trace.etl.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/2/10 4:46 下午
 */
public class NginxJaegerDomain {
    private String remoteAddr;
    private String nginxHostName;
    private String nginxIp;
    private String host;
    private String request;
    private String uri;
    private int status;
    private String refer;
    private String ua;
    private String xForwardedFor;
    private String upstreamAddr;
    private String requestTime;
    private String upstreamRespTime;
    private String upstreamStatus;
    private String traceId;
    private String spanId;
    private String startTime;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getNginxIp() {
        return nginxIp;
    }

    public void setNginxIp(String nginxIp) {
        this.nginxIp = nginxIp;
    }

    public String getNginxHostName() {
        return nginxHostName;
    }

    public void setNginxHostName(String nginxHostName) {
        this.nginxHostName = nginxHostName;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getRefer() {
        return refer;
    }

    public void setRefer(String refer) {
        this.refer = refer;
    }

    public String getUa() {
        return ua;
    }

    public void setUa(String ua) {
        this.ua = ua;
    }

    public String getxForwardedFor() {
        return xForwardedFor;
    }

    public void setxForwardedFor(String xForwardedFor) {
        this.xForwardedFor = xForwardedFor;
    }

    public String getUpstreamAddr() {
        return upstreamAddr;
    }

    public void setUpstreamAddr(String upstreamAddr) {
        this.upstreamAddr = upstreamAddr;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getUpstreamRespTime() {
        return upstreamRespTime;
    }

    public void setUpstreamRespTime(String upstreamRespTime) {
        this.upstreamRespTime = upstreamRespTime;
    }

    public String getUpstreamStatus() {
        return upstreamStatus;
    }

    public void setUpstreamStatus(String upstreamStatus) {
        this.upstreamStatus = upstreamStatus;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
