package com.xiaomi.youpin.docean.common;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2020/6/25
 */
public class MutableObject {

    private Object obj;

    public MutableObject() {
    }

    public MutableObject(Object obj) {
        this.obj = obj;
    }

    public <T> T getObj() {
        return (T)obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
