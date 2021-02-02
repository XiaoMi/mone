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

package com.xiaomi.youpin.tesla.agent.common;

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
                && !DOCKERHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }


    public static int getAvailablePort(int port) {
        if (port <= 0) {
            return getAvailablePort();
        }
        for (int i = port; i < MAX_PORT; i++) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(i);
                return i;
            } catch (IOException e) {
                // continue
            } finally {
                if (ss != null) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return port;
    }

    public static int getAvailableUdpPort(int port) {
        if (port <= 0) {
            return getUdpAvailablePort();
        }
        for (int i = port; i < MAX_PORT; i++) {
            DatagramSocket ss = null;
            try {
                ss = new DatagramSocket(i);
                return i;
            } catch (IOException e) {
                // continue
            } finally {
                if (ss != null) {
                    ss.close();
                }
            }
        }
        return port;
    }


    public static int getAvailablePort() {
        ServerSocket ss = null;
        try {
            ss = new ServerSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static int getUdpAvailablePort() {
        DatagramSocket ss = null;
        try {
            ss = new DatagramSocket();
            ss.bind(null);
            return ss.getLocalPort();
        } catch (IOException e) {
            return getRandomPort();
        } finally {
            if (ss != null) {
                ss.close();
            }
        }
    }


    public static int getRandomPort() {
        return RND_PORT_START + RANDOM.nextInt(RND_PORT_RANGE);
    }


    public static Set<Integer> getPorts(String path) {
        try {
            Pair<Integer, List<String>> res = ProcessUtils.process(path, "netstat -nat|grep LISTEN");
            return res.getValue().stream().map(it -> it.split("\\s+")).filter(it -> it.length > 3)
                    .map(it -> {
                        try {
                            String addr = it[3];
                            String[] ss = addr.split(":");
                            if (ss.length == 1) {
                                ss = addr.split("\\D");
                                return Integer.parseInt(ss[ss.length - 1]);
                            }

                            return Integer.parseInt(ss[ss.length - 1]);
                        } catch (Throwable ex) {
                            log.warn("error:{} {}", it, ex.getMessage());
                        }
                        return -1;
                    })
                    .filter(it -> it > 0)
                    .collect(Collectors.toSet());
        } catch (Throwable ex) {
            log.warn(ex.getMessage());
            return new HashSet<>();
        }
    }

}
