package com.xiaomi.hera.trace.etl.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/10/27 10:09 上午
 */
public class MetricsParseResult {

    private JaegerTracerDomain jaegerTracerDomain;

    private DriverDomain driverDomain;

    private boolean ignore;

    private boolean isValidate;

    private HeraTraceEtlConfig heraTraceEtlConfig;

    public MetricsParseResult(boolean ignore){
        this.ignore = ignore;
    }

    public MetricsParseResult(JaegerTracerDomain jaegerTracerDomain, DriverDomain driverDomain, boolean ignore, boolean isValidate, HeraTraceEtlConfig heraTraceEtlConfig){
        this.isValidate = isValidate;
        this.ignore = ignore;
        this.jaegerTracerDomain = jaegerTracerDomain;
        this.driverDomain = driverDomain;
        this.heraTraceEtlConfig = heraTraceEtlConfig;
    }

    public HeraTraceEtlConfig getHeraTraceEtlConfig() {
        return heraTraceEtlConfig;
    }

    public void setHeraTraceEtlConfig(HeraTraceEtlConfig heraTraceEtlConfig) {
        this.heraTraceEtlConfig = heraTraceEtlConfig;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public JaegerTracerDomain getJaegerTracerDomain() {
        return jaegerTracerDomain;
    }

    public void setJaegerTracerDomain(JaegerTracerDomain jaegerTracerDomain) {
        this.jaegerTracerDomain = jaegerTracerDomain;
    }

    public DriverDomain getDriverDomain() {
        return driverDomain;
    }

    public void setDriverDomain(DriverDomain driverDomain) {
        this.driverDomain = driverDomain;
    }

    public boolean isValidate() {
        return isValidate;
    }

    public void setValidate(boolean validate) {
        isValidate = validate;
    }
}
