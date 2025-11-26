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

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import java.util.*;

/**
 * @author goodjava@qq.com
 */
public class HttpSessionManager {

    private static ISessionStore getSessionStoreService(MvcContext mvcContext) {
        boolean clusterSession = false;
        if (null != mvcContext) {
            clusterSession = mvcContext.isClusterSession();
        }

        String beanName = clusterSession ? "ClusterSessionStore" : "SessionStore";
        return (ISessionStore) Ioc.ins().getBean(beanName);
    }

    @Deprecated
    public static String createSession() {
        return createSession(null);
    }

    public static String createSession(MvcContext mvcContext) {
        HttpSession session = new DefaultHttpSession();
        String sessionId = session.getId();
        ISessionStore sessionStoreService = getSessionStoreService(mvcContext);
        sessionStoreService.put(sessionId, session);

        return sessionId;
    }

    public static boolean isExists(String sessionId) {
        ISessionStore sessionStoreService = getSessionStoreService(null);
        if (sessionStoreService.containsKey(sessionId)) {
            HttpSession session = sessionStoreService.get(sessionId);
            if (session.getId() == null) {
                sessionStoreService.remove(sessionId);
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static void invalidate(String sessionId) {
        ISessionStore sessionStoreService = getSessionStoreService(null);
        sessionStoreService.remove(sessionId);
    }

    @Deprecated
    public static HttpSession getSession(String sessionId) {
        return getSessionStoreService(null).get(sessionId);
    }


    public static HttpSession getSession(MvcContext context) {
        String sid = getSessionId(context.getHeaders());
        ISessionStore sessionStoreService = getSessionStoreService(context);
        if (sid.equals("")) {
            String sessionId = createSession(context);
            context.setSessionId(sessionId);
            return sessionStoreService.getAndRefresh(sessionId);
        } else {
            return Optional.ofNullable(getSession(sid)).orElseGet(() -> {
                String sessionId = createSession(context);
                context.setSessionId(sessionId);
                return sessionStoreService.getAndRefresh(sessionId);
            });
        }

    }


    public static void setSessionId(MvcContext context, boolean exists, HttpResponse response) {
        if (!context.getSessionId().equals("")) {
            Cookie cookie = new DefaultCookie(HttpSession.SESSIONID, context.getSessionId());
            cookie.setPath("/");
            String encodeCookie = ServerCookieEncoder.STRICT.encode(cookie);
            response.headers().set(HttpHeaderNames.SET_COOKIE, encodeCookie);
            return;
        }

        if (exists == false) {
            Cookie cookie = new DefaultCookie(HttpSession.SESSIONID, HttpSessionManager.createSession(context));
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
