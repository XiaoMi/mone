package com.xiaomi.hera.trace.etl.domain;

import java.io.Serializable;
import java.util.Date;

public class HeraTraceEtlConfig implements Serializable {

    private Integer id;

    private String bindId;

    private String appName;

    private Integer baseInfoId;

    private String excludeMethod;

    private String excludeHttpserverMethod;

    private String excludeThread;

    private String excludeSql;

    private String excludeHttpUrl;

    private String excludeUa;

    private Integer httpSlowThreshold;

    private Integer dubboSlowThreshold;

    private Integer mysqlSlowThreshold;

    private Integer traceFilter;

    private Integer traceDurationThreshold;

    private String traceDebugFlag;

    private String httpStatusError;

    private Date createTime;

    private Date updateTime;

    private String createUser;

    private String updateUser;

    public Integer getTraceDurationThreshold() {
        return traceDurationThreshold;
    }

    public void setTraceDurationThreshold(Integer traceDurationThreshold) {
        this.traceDurationThreshold = traceDurationThreshold;
    }

    public String getTraceDebugFlag() {
        return traceDebugFlag;
    }

    public void setTraceDebugFlag(String traceDebugFlag) {
        this.traceDebugFlag = traceDebugFlag;
    }

    public Integer getTraceFilter() {
        return traceFilter;
    }

    public void setTraceFilter(Integer traceFilter) {
        this.traceFilter = traceFilter;
    }

    public String getBindId() {
        return bindId;
    }

    public void setBindId(String bindId) {
        this.bindId = bindId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBaseInfoId() {
        return baseInfoId;
    }

    public void setBaseInfoId(Integer baseInfoId) {
        this.baseInfoId = baseInfoId;
    }

    public String getExcludeMethod() {
        return excludeMethod;
    }

    public void setExcludeMethod(String excludeMethod) {
        this.excludeMethod = excludeMethod == null ? null : excludeMethod.trim();
    }

    public String getExcludeHttpserverMethod() {
        return excludeHttpserverMethod;
    }

    public void setExcludeHttpserverMethod(String excludeHttpserverMethod) {
        this.excludeHttpserverMethod = excludeHttpserverMethod == null ? null : excludeHttpserverMethod.trim();
    }

    public String getExcludeThread() {
        return excludeThread;
    }

    public void setExcludeThread(String excludeThread) {
        this.excludeThread = excludeThread == null ? null : excludeThread.trim();
    }

    public String getExcludeSql() {
        return excludeSql;
    }

    public void setExcludeSql(String excludeSql) {
        this.excludeSql = excludeSql == null ? null : excludeSql.trim();
    }

    public String getExcludeHttpUrl() {
        return excludeHttpUrl;
    }

    public void setExcludeHttpUrl(String excludeHttpUrl) {
        this.excludeHttpUrl = excludeHttpUrl == null ? null : excludeHttpUrl.trim();
    }

    public String getExcludeUa() {
        return excludeUa;
    }

    public void setExcludeUa(String excludeUa) {
        this.excludeUa = excludeUa == null ? null : excludeUa.trim();
    }

    public Integer getHttpSlowThreshold() {
        return httpSlowThreshold;
    }

    public void setHttpSlowThreshold(Integer httpSlowThreshold) {
        this.httpSlowThreshold = httpSlowThreshold;
    }

    public Integer getDubboSlowThreshold() {
        return dubboSlowThreshold;
    }

    public void setDubboSlowThreshold(Integer dubboSlowThreshold) {
        this.dubboSlowThreshold = dubboSlowThreshold;
    }

    public Integer getMysqlSlowThreshold() {
        return mysqlSlowThreshold;
    }

    public void setMysqlSlowThreshold(Integer mysqlSlowThreshold) {
        this.mysqlSlowThreshold = mysqlSlowThreshold;
    }

    public String getHttpStatusError() {
        return httpStatusError;
    }

    public void setHttpStatusError(String httpStatusError) {
        this.httpStatusError = httpStatusError == null ? null : httpStatusError.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser == null ? null : createUser.trim();
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser == null ? null : updateUser.trim();
    }

    @Override
    public String toString() {
        return "HeraTraceEtlConfig{" +
                "id=" + id +
                ", bindId='" + bindId + '\'' +
                ", appName='" + appName + '\'' +
                ", baseInfoId=" + baseInfoId +
                ", excludeMethod='" + excludeMethod + '\'' +
                ", excludeHttpserverMethod='" + excludeHttpserverMethod + '\'' +
                ", excludeThread='" + excludeThread + '\'' +
                ", excludeSql='" + excludeSql + '\'' +
                ", excludeHttpUrl='" + excludeHttpUrl + '\'' +
                ", excludeUa='" + excludeUa + '\'' +
                ", httpSlowThreshold=" + httpSlowThreshold +
                ", dubboSlowThreshold=" + dubboSlowThreshold +
                ", mysqlSlowThreshold=" + mysqlSlowThreshold +
                ", traceFilter=" + traceFilter +
                ", traceDurationThreshold=" + traceDurationThreshold +
                ", traceDebugFlag='" + traceDebugFlag + '\'' +
                ", httpStatusError='" + httpStatusError + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createUser='" + createUser + '\'' +
                ", updateUser='" + updateUser + '\'' +
                '}';
    }
}