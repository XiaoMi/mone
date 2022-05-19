package com.xiaomi.youpin.docean.mvc.session;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author goodjava@qq.com
 */
@Data
public class DefaultHttpSession implements HttpSession {

    private String sessionId;
    private Map<String, Object> attributes = new HashMap<>();
    private long createTime;
    private long updateTime;

    public DefaultHttpSession() {
        long now = System.currentTimeMillis();
        this.sessionId = UUID.randomUUID().toString();
        createTime = now;
        updateTime = now;
    }

    @Override
    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        this.attributes.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    @Override
    public String getId() {
        return this.sessionId;
    }

    @Override
    public void invalidate() {
        this.sessionId = null;
    }


}
