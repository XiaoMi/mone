package com.xiaomi.data.push.dao.model;

/**
 * @author goodjava@qq.com
 */
public class ActionConf {
    private Long id;

    private String action;

    private String method;

    private Byte needCache;

    private Byte needLog;

    private Byte online;

    private String addr;

    private Byte mock;

    private Long ctime;

    private Long utime;

    private Integer version;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action == null ? null : action.trim();
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public Byte getNeedCache() {
        return needCache;
    }

    public void setNeedCache(Byte needCache) {
        this.needCache = needCache;
    }

    public Byte getNeedLog() {
        return needLog;
    }

    public void setNeedLog(Byte needLog) {
        this.needLog = needLog;
    }

    public Byte getOnline() {
        return online;
    }

    public void setOnline(Byte online) {
        this.online = online;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr == null ? null : addr.trim();
    }

    public Byte getMock() {
        return mock;
    }

    public void setMock(Byte mock) {
        this.mock = mock;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getUtime() {
        return utime;
    }

    public void setUtime(Long utime) {
        this.utime = utime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}