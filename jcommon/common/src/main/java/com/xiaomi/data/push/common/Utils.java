package com.xiaomi.data.push.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by zhangzhiyong on 30/05/2018.
 */
public abstract class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String getIp() {
        try {
            Enumeration allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        String ipStr = ip.getHostAddress();
                        if (ipStr.startsWith("172.17")) {
                            continue;
                        }
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return "";
        }
        return "";
    }

}
