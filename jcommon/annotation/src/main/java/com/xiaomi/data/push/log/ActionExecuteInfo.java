package com.xiaomi.data.push.log;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 */
public class ActionExecuteInfo {

    /**
     * 执行的次数
     */
    @Getter
    private AtomicLong executeNum = new AtomicLong(0);

    /**
     * 执行成功的次数
     */
    private AtomicLong successNum = new AtomicLong(0);

    /**
     * 执行失败的次数
     */
    private AtomicLong failureNum = new AtomicLong(0);


    @Getter
    private AtomicLong rt = new AtomicLong();


    public AtomicLong getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(AtomicLong successNum) {
        this.successNum = successNum;
    }

    public AtomicLong getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(AtomicLong failureNum) {
        this.failureNum = failureNum;
    }
}
