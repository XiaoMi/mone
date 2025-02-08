package com.xiaomi.youpin.docean.mvc.session;

import java.util.List;

/**
 * @author shanwb
 * @date 2024-09-03
 */
public interface ISessionStore {

    void put(String sessionId, HttpSession session);

    void remove(String sessionId);

    List<String> sessionIdList();

    HttpSession get(String sessionId);

    HttpSession getAndRefresh(String sessionId);

    boolean containsKey(String sessionId);

}
