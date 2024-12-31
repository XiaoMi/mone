package com.xiaomi.youpin.tesla.ip.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class NetUtils {


    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static final String LOCALHOST = "127.0.0.1";
    public static final String ANYHOST = "0.0.0.0";
    public static final String DOCKERHOST = "172.17.0.1";

    public static final String LOCALHOST2 = "192.";



    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final int RND_PORT_START = 30000;

    private static final int MAX_PORT = 65535;

    private static final Random RANDOM = new Random(System.currentTimeMillis());

    private static final int RND_PORT_RANGE = 10000;


    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("get host name error:{}", e.getMessage());
            return "";
        }
    }


    public static String getLocalHost() {
        InetAddress address = getLocalAddress();
        return address == null ? LOCALHOST : address.getHostAddress();
    }

    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (localAddress instanceof Inet6Address) {
                Inet6Address address = (Inet6Address) localAddress;
                if (isValidV6Address(address)) {
                    return normalizeV6Address(address);
                }
            } else if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            log.warn(e.getMessage());
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (null == interfaces) {
                return localAddress;
            }
            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            if (address instanceof Inet6Address) {
                                Inet6Address v6Address = (Inet6Address) address;
                                if (isValidV6Address(v6Address)) {
                                    return normalizeV6Address(v6Address);
                                }
                            } else if (isValidAddress(address)) {
                                return address;
                            }
                        } catch (Throwable e) {
                            log.warn(e.getMessage());
                        }
                    }
                } catch (Throwable e) {
                    log.warn(e.getMessage());
                }
            }
        } catch (Throwable e) {
            log.warn(e.getMessage());
        }
        return localAddress;
    }

    static boolean isValidV6Address(Inet6Address address) {
        boolean preferIpv6 = Boolean.getBoolean("java.net.preferIPv6Addresses");
        if (!preferIpv6) {
            return false;
        }
        try {
            return address.isReachable(100);
        } catch (IOException e) {
            // ignore
        }
        return false;
    }

    static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                // ignore
                log.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }


    static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && !name.startsWith(LOCALHOST2)
                && !DOCKERHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }

}
