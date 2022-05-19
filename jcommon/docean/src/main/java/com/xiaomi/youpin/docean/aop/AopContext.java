package com.xiaomi.youpin.docean.aop;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author goodjava@qq.com
 */
public class AopContext {

    @Getter
    private Map<String, Object> attachments = new HashMap<>();


    public <T> T get(String key) {
        return (T) attachments.get(key);
    }

    public void put(String key, Object value) {
        attachments.put(key, value);
    }
}
