package com.xiaomi.data.push.action;

import lombok.Getter;
import lombok.Setter;

/**
 * @author goodjava@qq.com
 * <p>
 * action 可以控制注解实际的效果
 * 比如是否启用cache 是否启用 log 是否启用 mock
 */
public class ActionInfo {

    /**
     * action 名称
     */
    private String name;


    /**
     * 是method 级别的,因此这个name实际就是 method.toString
     */
    private String methodName;

    /**
     * 是否记录日志
     */
    private boolean recordLog = true;

    /**
     * 是否缓存
     */
    private boolean cache;

    /**
     * 是否使用mock数据
     */
    private boolean mock;

    /**
     * 上下线状态(1 是上线 0 是 下线)
     */
    @Setter
    @Getter
    private Byte online = 1;

    /**
     * action的版本
     */
    private int version;

    @Getter
    @Setter
    private String serverInfo;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRecordLog() {
        return recordLog;
    }

    public void setRecordLog(boolean recordLog) {
        this.recordLog = recordLog;
    }

    public boolean isCache() {
        return cache;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }

    public boolean isMock() {
        return mock;
    }

    public void setMock(boolean mock) {
        this.mock = mock;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}
