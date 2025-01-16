package run.mone.m78.common;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.Iterator;
import java.util.Set;

public class CookieHelper {
    
    public static String extractToken(String cookieStr, String key) {

        Set<Cookie> cookieSet = ServerCookieDecoder.LAX.decode(cookieStr);
        Iterator<Cookie> iter = cookieSet.iterator();
        while (iter.hasNext()) {
            Cookie cookie = iter.next();
            if (key.equals(cookie.name())) {
              return cookie.value();
            }
        }
        return null;
    }
}