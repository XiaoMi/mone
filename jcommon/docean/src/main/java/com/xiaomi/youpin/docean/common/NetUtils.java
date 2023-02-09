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

package com.xiaomi.youpin.docean.common;

import io.netty.channel.epoll.Epoll;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * @author goodjava@qq.com
 * @date 2020/6/21
 */
@Slf4j
public abstract class NetUtils {

    public static final String LOCALHOST = "127.0.0.1";

    private static boolean isLinuxPlatform = false;

    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static final String OS_NAME = System.getProperty("os.name");


    public static final String ANYHOST = "0.0.0.0";
    public static final String DOCKERHOST = "172.17.0.1";


    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");


    static {
        if (OS_NAME != null && OS_NAME.toLowerCase().indexOf("linux") >= 0) {
            isLinuxPlatform = true;
        }
    }

    public static boolean useEpoll() {
        return isLinuxPlatform() && Epoll.isAvailable();
    }


    public static boolean isLinuxPlatform() {
        return isLinuxPlatform;
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


    static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();

        if (null != name && name.startsWith("172")) {
            return false;
        }


        return (name != null
                && !ANYHOST.equals(name)
                && !LOCALHOST.equals(name)
                && !DOCKERHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
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

}
