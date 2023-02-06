package com.xiaomi.youpin.docean.listener.event;

/**
 * @author goodjava@qq.com
 */

public enum EventType {

    initBegin("initBegin"),
    addBean("addBean"),
    putBean("putBean"),
    removeBean("removeBean"),
    initBean("initBean"),
    initFinish("initFinish"),
    mvcBegin("mvcBegin"),
    initController("initController"),
    mvcUploadFinish("mvcUploadFinish"),
    custom("custom");

    private String name;

    private EventType(String name) {
        this.name = name;
    }

}
