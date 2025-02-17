package com.xiaomi.youpin.docean.mvc.session.impl;

import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.mvc.session.DefaultHttpSession;
import com.xiaomi.youpin.docean.mvc.session.HttpSession;
import com.xiaomi.youpin.docean.mvc.session.ISessionStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author shanwb
 * @date 2024-09-03
 */
@Component(name = "SessionStore")
public class LocalSessionStore implements ISessionStore {

    private static final Map<String, HttpSession> SESSION_MAP = new ConcurrentHashMap<>();

    static {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Safe.run(() -> {
                long now = System.currentTimeMillis();
                List<String> ids = SESSION_MAP.values().stream().filter(it -> {
                    DefaultHttpSession hs = (DefaultHttpSession) it;
                    if (now - hs.getUpdateTime() > TimeUnit.MINUTES.toMillis(60)) {
                        return true;
                    }
                    return false;
                }).map(HttpSession::getId).toList();
                ids.forEach(SESSION_MAP::remove);
            }, e -> {
            });
        }, 10, 5, TimeUnit.SECONDS);
    }

    @Override
    public void put(String sessionId, HttpSession session) {
        SESSION_MAP.put(sessionId, session);
    }

    @Override
    public void remove(String sessionId) {
        SESSION_MAP.remove(sessionId);
    }

    @Override
    public List<String> sessionIdList() {
        return new ArrayList<>(SESSION_MAP.keySet());
    }

    @Override
    public HttpSession get(String sessionId) {
        return SESSION_MAP.get(sessionId);
    }

    @Override
    public HttpSession getAndRefresh(String sessionId) {
        return SESSION_MAP.compute(sessionId, (k, v) -> {
            if (null != v) {
                if (v instanceof DefaultHttpSession dhs) {
                    dhs.setUpdateTime(System.currentTimeMillis());
                }
            }
            return v;
        });
    }

    @Override
    public boolean containsKey(String sessionId) {
        return SESSION_MAP.containsKey(sessionId);
    }
}
