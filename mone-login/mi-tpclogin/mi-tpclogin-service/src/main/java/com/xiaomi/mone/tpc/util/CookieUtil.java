package com.xiaomi.mone.tpc.util;

import com.xiaomi.mone.tpc.login.util.ConstUtil;
import com.xiaomi.mone.tpc.login.vo.AuthUserVo;

/**
 * @project: mi-tpclogin
 * @author: zgf1
 * @date: 2022/11/9 9:34
 */
public class CookieUtil {
    /**
     * cookie设置跳转
     * @param userVo
     * @param pageUrl
     * @throws Throwable
     */
    public static void setCookieUrl(AuthUserVo userVo, String pageUrl) throws Throwable {
        StringBuilder tokenArg = new StringBuilder();
        tokenArg.append(ConstUtil.AUTH_TOKEN).append("=").append(userVo.getToken());
        StringBuilder setCookieUrl = new StringBuilder();
        int pos = pageUrl.indexOf(ConstUtil.AUTH_TOKEN);
        if (pos > 0) {
            setCookieUrl.append(pageUrl.substring(0, pos));
            setCookieUrl.append(tokenArg);
            String subPageUrl = pageUrl.substring(pos);
            pos = subPageUrl.indexOf('#');
            if (pos > 0) {
                setCookieUrl.append(subPageUrl.substring(pos));
                userVo.setSetCookUrl(setCookieUrl.toString());
                return;
            }
            pos = subPageUrl.indexOf('&');
            if (pos > 0) {
                setCookieUrl.append(subPageUrl.substring(pos));
                userVo.setSetCookUrl(setCookieUrl.toString());
                return;
            }
            userVo.setSetCookUrl(setCookieUrl.toString());
            return;
        }
        pos = pageUrl.indexOf('#');
        if (pos > 0) {
            setCookieUrl.append(pageUrl.substring(0, pos));
            setCookieUrl.append("?");
            setCookieUrl.append(tokenArg);
            setCookieUrl.append(pageUrl.substring(pos));
        } else {
            setCookieUrl.append(pageUrl);
            if(pageUrl.contains("?")) {
                setCookieUrl.append("&");
                setCookieUrl.append(tokenArg);
            } else {
                setCookieUrl.append("?");
                setCookieUrl.append(tokenArg);
            }
        }
        userVo.setSetCookUrl(setCookieUrl.toString());
    }
}
