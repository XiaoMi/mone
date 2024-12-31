/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.docean.mvc.session;

import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public class HttpSessionManager {


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
                }).map(it -> it.getId()).collect(Collectors.toList());
                ids.forEach(id -> SESSION_MAP.remove(id));
            }, e -> {
            });
        }, 10, 5, TimeUnit.SECONDS);
    }

    public static String createSession() {
        HttpSession session = new DefaultHttpSession();
        String sessionId = session.getId();
        SESSION_MAP.put(sessionId, session);
        return sessionId;
    }

    public static boolean isExists(String sessionId) {
        if (SESSION_MAP.containsKey(sessionId)) {
            HttpSession session = SESSION_MAP.get(sessionId);
            if (session.getId() == null) {
                SESSION_MAP.remove(sessionId);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static void invalidate(String sessionId) {
        SESSION_MAP.remove(sessionId);
    }

    public static HttpSession getSession(String sessionId) {
        return SESSION_MAP.compute(sessionId, (k, v) -> {
            if (null != v) {
                if (v instanceof DefaultHttpSession dhs) {
                    dhs.setUpdateTime(System.currentTimeMillis());
                }
            }
            return v;
        });
    }


    public static HttpSession getSession(MvcContext context) {
        String sid = getSessionId(context.getHeaders());
        if (sid.equals("")) {
            String session = createSession();
            context.setSessionId(session);
            return getSession(session);
        } else {
            return Optional.ofNullable(getSession(sid)).orElseGet(() -> {
                String session = createSession();
                context.setSessionId(session);
                return getSession(session);
            });
        }

    }


    public static void setSessionId(MvcContext context, boolean exists, FullHttpResponse response) {
        if (!context.getSessionId().equals("")) {
            Cookie cookie = new DefaultCookie(HttpSession.SESSIONID, context.getSessionId());
            cookie.setPath("/");
            String encodeCookie = ServerCookieEncoder.STRICT.encode(cookie);
            response.headers().set(HttpHeaderNames.SET_COOKIE, encodeCookie);
            return;
        }

        if (exists == false) {
            Cookie cookie = new DefaultCookie(HttpSession.SESSIONID, HttpSessionManager.createSession());
            cookie.setPath("/");
            String encodeCookie = ServerCookieEncoder.STRICT.encode(cookie);
            response.headers().set(HttpHeaderNames.SET_COOKIE, encodeCookie);
        }
    }


    public static boolean isHasSessionId(Map<String, String> headers) {
        String cookieStr = headers.get("Cookie");
        if (cookieStr == null || "".equals(cookieStr)) {
            if (!headers.containsKey("cookie")) {
                return false;
            }
            cookieStr = headers.get("cookie");
        }
        Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieStr);
        Iterator<Cookie> iter = cookieSet.iterator();
        while (iter.hasNext()) {
            Cookie cookie = iter.next();
            if (HttpSession.SESSIONID.equals(cookie.name())) {
                if (HttpSessionManager.isExists(cookie.value())) {
                    return true;
                }
            }
        }
        return false;
    }


    public static String getSessionId(Map<String, String> headers) {
        String cookieStr = headers.get("Cookie");
        if (cookieStr == null || "".equals(cookieStr)) {
            cookieStr = headers.get("cookie");
            if (cookieStr == null || "".equals(cookieStr)) {
                return "";
            }
        }
        Set<Cookie> cookieSet = ServerCookieDecoder.STRICT.decode(cookieStr);
        Iterator<Cookie> iter = cookieSet.iterator();
        while (iter.hasNext()) {
            Cookie cookie = iter.next();
            if (HttpSession.SESSIONID.equals(cookie.name())) {
                return cookie.value();

            }
        }
        return "";
    }


}
