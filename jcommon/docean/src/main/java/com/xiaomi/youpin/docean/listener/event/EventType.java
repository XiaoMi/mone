package com.xiaomi.youpin.docean.listener.event;

/**
 * @author goodjava@qq.com
 */

public enum EventType {

    initBegin("initBegin"),
    addBean("addBean"),
    putBean("putBean"),
    initFinish("initFinish"),
    mvcBegin("mvcBegin"),
    initController("initController"),
    custom("custom");

    private String name;

    private EventType(String name) {
        this.name = name;
    }

}
