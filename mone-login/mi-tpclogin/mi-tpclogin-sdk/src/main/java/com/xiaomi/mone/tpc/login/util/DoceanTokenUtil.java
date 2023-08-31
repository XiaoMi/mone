package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.vo.AuthTokenVo;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import com.xiaomi.youpin.docean.mvc.MvcContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DoceanTokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(DoceanTokenUtil.class);

    public static void setCookie(AuthUserVo userVo, MvcContext mvcContext) {
     StringBuilder setCookie = new StringBuilder();
        setCookie.append(ConstUtil.AUTH_TOKEN).append("=").append(userVo.getToken());
        setCookie.append("; ");
        String origin = Optional.ofNullable(mvcContext.getHeaders().get("Origin")).orElse(mvcContext.getHeaders().get("origin"));
        if (StringUtils.isBlank(origin)) {
            origin = Optional.ofNullable(mvcContext.getHeaders().get("Host")).orElse(mvcContext.getHeaders().get("host"));
        }
        String domain = HostUtil.getDomain(origin);
        if (StringUtils.isNotBlank(domain)) {
            setCookie.append("domain=").append(domain);
            setCookie.append("; ");
        }
        setCookie.append("MaxAge=").append(userVo.getExprTime());
        setCookie.append("; ");
        setCookie.append("Path=/");
        mvcContext.getResHeaders().put("Set-Cookie", setCookie.toString());
    }

    public static final AuthTokenVo parseAuthToken(MvcContext mvcContext) {
        if (mvcContext.getParams() != null && mvcContext.getParams().getAsJsonObject().get(ConstUtil.AUTH_TOKEN) != null) {
            String authToken = mvcContext.getParams().getAsJsonObject().get(ConstUtil.AUTH_TOKEN).getAsString();
            if (StringUtils.isNotBlank(authToken)) {
                AuthTokenVo tokenVo = new AuthTokenVo();
                tokenVo.setAuthToken(authToken);
                tokenVo.setFromCookie(false);
                return tokenVo;
            }
        }
        String referer = Optional.ofNullable(mvcContext.getHeaders().get("Referer")).orElse(mvcContext.getHeaders().get("referer"));
        logger.info("referer={}", referer);
        if (StringUtils.isBlank(referer)) {
            return getAuthToken(mvcContext);
        }
        String[] arr = referer.split("\\?");
        if (arr.length != 2) {
            return getAuthToken(mvcContext);
        }
        Map<String, String> params = new HashMap<>();
        String[] kvArr = arr[1].split("\\&");
        for (String kv : kvArr) {
            String[] subKvArr = kv.split("\\=");
            if (subKvArr.length != 2) {
                continue;
            }
            if (!subKvArr[0].equals(ConstUtil.AUTH_TOKEN)) {
                continue;
            }
            AuthTokenVo tokenVo = new AuthTokenVo();
            tokenVo.setAuthToken(subKvArr[1]);
            tokenVo.setFromCookie(false);
            return tokenVo;
        }
        return getAuthToken(mvcContext);
    }

    /**
     * 获取cookie值
     * @param mvcContext
     * @return
     */
    private static final AuthTokenVo getAuthToken(MvcContext mvcContext) {
        String cookieStr = Optional.ofNullable(mvcContext.getHeaders().get("Cookie")).orElse(mvcContext.getHeaders().get("cookie"));
        if (StringUtils.isBlank(cookieStr)) {
            return null;
        }
        String[] cookies = cookieStr.split("; ");
        for (String cookie : cookies) {
            String[] kv = cookie.split("=");
            if (kv == null || kv.length != 2) {
                return null;
            }
            if (kv[0].equals(ConstUtil.AUTH_TOKEN)) {
                AuthTokenVo tokenVo = new AuthTokenVo();
                tokenVo.setAuthToken(kv[1]);
                tokenVo.setFromCookie(true);
                return tokenVo;
            }
        }
        return null;
    }

}
