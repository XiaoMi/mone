package com.xiaomi.mone.tpc.login.util;

import com.xiaomi.mone.tpc.login.vo.AuthUserVo;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;

/**
 * @project: mi-tpclogin
 * @author: zgf1
 * @date: 2022/10/27 18:48
 */
public class Auth2Util {

    /**
     * cookie设置跳转
     * @param userVo
     * @param pageUrl
     * @throws Throwable
     */
    public static void setCookieUrl(AuthUserVo userVo, String pageUrl) throws Throwable {
        StringBuilder setCookieUrl = new StringBuilder();
        setCookieUrl.append(pageUrl);
        URL url = new URL(pageUrl);
        String params = url.getQuery();
        if (StringUtils.isBlank(params)) {
            if (pageUrl.endsWith("?")) {
                setCookieUrl.append("code=").append(userVo.getCode());
            } else {
                setCookieUrl.append("?code=").append(userVo.getCode());
            }
        } else {
            setCookieUrl.append("&code=").append(userVo.getToken());
        }
        if (StringUtils.isNotBlank(userVo.getState())) {
            setCookieUrl.append("&state=").append(userVo.getState());
        }
        userVo.setSetCookUrl(setCookieUrl.toString());
    }

    public static String genVcode(Object... params) {
        StringBuilder vcode = new StringBuilder();
        vcode.append(System.currentTimeMillis());
        if (params == null || params.length == 0) {
            return vcode.toString();
        }
        for (Object param : params) {
            if (param == null) {
                continue;
            }
            vcode.append(param);
        }
        return MD5Util.md5(vcode.toString());
    }
}
