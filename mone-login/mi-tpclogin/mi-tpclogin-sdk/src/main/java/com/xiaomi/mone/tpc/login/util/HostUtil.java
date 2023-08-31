package com.xiaomi.mone.tpc.login.util;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * @project: mi-tpclogin
 * @author: zgf1
 * @date: 2022/10/27 18:48
 */
public class HostUtil {

    private static final Pattern ipv4Regx = Pattern.compile("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");
    private static final Pattern ipv6Regx = Pattern.compile("^([\\da-fA-F]{1,4}:){6}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^::([\\da-fA-F]{1,4}:){0,4}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:):([\\da-fA-F]{1,4}:){0,3}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){2}:([\\da-fA-F]{1,4}:){0,2}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){3}:([\\da-fA-F]{1,4}:){0,1}((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){4}:((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$|^([\\da-fA-F]{1,4}:){7}[\\da-fA-F]{1,4}$|^:((:[\\da-fA-F]{1,4}){1,6}|:)$|^[\\da-fA-F]{1,4}:((:[\\da-fA-F]{1,4}){1,5}|:)$|^([\\da-fA-F]{1,4}:){2}((:[\\da-fA-F]{1,4}){1,4}|:)$|^([\\da-fA-F]{1,4}:){3}((:[\\da-fA-F]{1,4}){1,3}|:)$|^([\\da-fA-F]{1,4}:){4}((:[\\da-fA-F]{1,4}){1,2}|:)$|^([\\da-fA-F]{1,4}:){5}:([\\da-fA-F]{1,4})?$|^([\\da-fA-F]{1,4}:){6}:$");

    public static final String getDomain(String origin) {
        if (StringUtils.isBlank(origin)) {
            return null;
        }
        String host = null;
        try {
            boolean hasProtocol = origin.startsWith("http://") || origin.startsWith("https://");
            if (!hasProtocol) {
                origin = "http://" + origin;
            }
            host = new URL(origin).getHost();
        } catch (MalformedURLException e) {
        }
        if (StringUtils.isBlank(host)) {
            return null;
        }
        if (ipv4Regx.matcher(host).find()) {
            return null;
        }
        if (ipv6Regx.matcher(host).find()) {
            return null;
        }
        int findCt = 0;
        StringBuilder domain = new StringBuilder();
        char[] cs = host.toCharArray();
        for (int i = cs.length - 1; i >= 0; i--) {
            if (cs[i] == '.') {
                if (++findCt >= 2) {
                    break;
                }
            }
            domain.append(cs[i]);
        }
        if (findCt >= 2) {
            return domain.reverse().toString();
        }
        return host;
    }

}
