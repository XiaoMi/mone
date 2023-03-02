package com.xiaomi.hera.trace.etl.domain;

public class ErrorTraceMessageToEs {
    /**
     * 固定值：jaegerquery
     */
    private String domain;
    /**
     * 请求类型，目前有四种：
     * http：对应http请求
     * dubbo_consumer：dubbo consumer端
     * dubbo_provider：dubbo provider端
     * redis：对应Redis请求
     * mysql：对应MySQL请求
     */
    private String type;

    /**
     * 服务端物理机的ip，
     */
    private String host;

    /**
     * http请求：为请求的URI eg：/ok
     * dubbo请求：为请求的service/method eg：com.xiaomi.member.provider.MemberService/getMemberById
     * redis请求：redis的命令 key，截取前两百个字符  eg：MGET key1 key2 key3....
     * mysql请求：sql截取前两百个字符  eg：select id,name,gender from user where ....
     */
    private String url;

    /**
     * http请求：传空字符串
     * dubbo请求：传空字符串
     * redis请求：redis服务端的ip:port  eg: 127.0.0.1:6379
     * mysql请求：mysql服务端的ip:port/dbName  eg: 127.0.0.1:3306/testDB
     */
    private String dataSource;

    /**
     * 服务名
     */
    private String serviceName;

    private String traceId;

    /**
     * 请求结束时间戳，单位：毫秒
     */
    private String timestamp;

    /**
     * 请求耗时，单位：毫秒
     */
    private String duration;

    /**
     * 错误类型有两种：
     * error
     * timeout
     */
    private String errorType;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }
}
