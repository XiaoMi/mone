package com.xiaomi.data.push.uds.listener;

import lombok.Data;


/**
 * @author goodjava@qq.com
 */
@Data
public class UdsEvent {

    private String app;

    private Object data;

    public UdsEvent(String app, Object data) {
        this.app = app;
        this.data = data;
    }

}
