package com.xiaomi.data.push.dao.model;

import java.io.Serializable;

/**
 * 　　* @description: TODO
 * 　　* @author zhenghao
 *
 */
public class TaskHistoryExample implements Serializable {

    private long time;

    private boolean result;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TaskHistoryExample{");
        sb.append("time=").append(time);
        sb.append(", result=").append(result);
        sb.append('}');
        return sb.toString();
    }
}