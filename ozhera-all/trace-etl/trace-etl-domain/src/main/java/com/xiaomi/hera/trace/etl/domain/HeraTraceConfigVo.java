package com.xiaomi.hera.trace.etl.domain;

/**
 * @Description
 * @Author dingtao
 * @Date 2022/4/19 10:16 上午
 */
public class HeraTraceConfigVo extends PagerVo{
    private Integer baseInfoId;

    private String user;

    private Integer bindId;

    private String appName;

    public Integer getBaseInfoId() {
        return baseInfoId;
    }

    public void setBaseInfoId(Integer baseInfoId) {
        this.baseInfoId = baseInfoId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getBindId() {
        return bindId;
    }

    public void setBindId(Integer bindId) {
        this.bindId = bindId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public String toString() {
        return "HeraTraceConfigVo{" +
                "baseInfoId=" + baseInfoId +
                ", page=" + getPage() +
                ", pageSize=" + getPageSize() +
                '}';
    }
}
