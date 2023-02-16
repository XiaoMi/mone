package com.xiaomi.hera.trace.etl.domain;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

/**
 * @Description
 * @Author dingtao
 */
public class JaegerTracerDomain {
    private String serviceName;
    private String traceId;
    private long startTime;
    private long endTime;
    private String type;
    private String serverIp;
    private String method;
    private String statement;
    private String dbHost;
    private String dbPort;
    private String httpCode;
    private String serviceEnv;
    private String serviceEnvId;
    /**
     * mifaas
     */
    private String functionName;
    private String functionModule;
    private String functionId;
    /**
     * redis
     */
    private String key;
    /**
     * mysql
     */
    private String dataSource;
    private String sqlMethod;
    private String sql;
    private String dbName;
    /**
     * dubbo
     */
    private String dubboServiceName;
    /**
     * rocketmq
     */
    private String topic;
    /**
     * 对应SpanKind类
     */
    private String kind;

    private double duration;
    private BigDecimal denominator = new BigDecimal(1000.0);
    private boolean isSuccess = true;

    /**
     * 供压测全链路监控使用
     */
    private String miMeterSceneId;
    private String miMeterTraceId;
    private String miMeterInterfaceId;
    private String metricsServiceName;

    public String getFunctionId() {
        return functionId == null ? "" : functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getMetricsServiceName() {
        return metricsServiceName;
    }

    public void setMetricsServiceName(String metricsServiceName) {
        this.metricsServiceName = metricsServiceName;
    }

    public String getMiMeterSceneId() {
        return miMeterSceneId == null ? "" : miMeterSceneId;
    }

    public void setMiMeterSceneId(String miMeterSceneId) {
        this.miMeterSceneId = miMeterSceneId;
    }

    public String getMiMeterTraceId() {
        return miMeterTraceId == null ? "" : miMeterTraceId;
    }

    public void setMiMeterTraceId(String miMeterTraceId) {
        this.miMeterTraceId = miMeterTraceId;
    }

    public String getMiMeterInterfaceId() {
        return miMeterInterfaceId == null ? "" : miMeterInterfaceId;
    }

    public void setMiMeterInterfaceId(String miMeterInterfaceId) {
        this.miMeterInterfaceId = miMeterInterfaceId;
    }

    public String getServiceEnvId() {
        return serviceEnvId == null ? "" : serviceEnvId;
    }

    public void setServiceEnvId(String serviceEnvId) {
        this.serviceEnvId = serviceEnvId;
    }

    public String getFunctionName() {
        return functionName == null ? "" : functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getFunctionModule() {
        return functionModule == null ? "" : functionModule;
    }

    public void setFunctionModule(String functionModule) {
        this.functionModule = functionModule;
    }

    public String getServiceEnv() {
        return serviceEnv == null ? "" : serviceEnv;
    }

    public void setServiceEnv(String serviceEnv) {
        this.serviceEnv = serviceEnv;
    }

    public String getHttpCode() {
        return StringUtils.isEmpty(httpCode) ? "500" : httpCode;
    }

    public void setHttpCode(String httpCode) {
        this.httpCode = httpCode;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getDbHost() {
        return dbHost == null ? "" : dbHost;
    }

    public void setDbHost(String dbHost) {
        this.dbHost = dbHost;
    }

    public String getDbPort() {
        return dbPort == null ? "" : dbPort;
    }

    public void setDbPort(String dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbName() {
        return dbName == null ? "" : dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDubboServiceName() {
        return dubboServiceName;
    }

    public void setDubboServiceName(String dubboServiceName) {
        this.dubboServiceName = dubboServiceName;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDataSource() {
        return dataSource == null ? "" : dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getSqlMethod() {
        return sqlMethod == null ? "" : sqlMethod;
    }

    public void setSqlMethod(String sqlMethod) {
        this.sqlMethod = sqlMethod;
    }

    public String getSql() {
        return sql == null ? "" : sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getKey() {
        return key == null ? "" : key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getServerIp() {
        return serverIp == null ? "" : serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getServiceName() {
        return serviceName == null ? "" : serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method == null ? "" : method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        BigDecimal durationBig = new BigDecimal(duration);
        this.duration = durationBig.divide(denominator, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public String getStatement() {
        return statement == null ? "" : statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "JaegerTracerDomain{" +
                "serviceName='" + serviceName + '\'' +
                ", type='" + type + '\'' +
                ", method='" + method + '\'' +
                ", statement='" + statement + '\'' +
                ", duration=" + duration +
                '}';
    }
}
