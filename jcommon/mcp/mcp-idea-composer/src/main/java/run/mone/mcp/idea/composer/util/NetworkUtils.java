package run.mone.mcp.idea.composer.util;

import java.net.*;
import java.util.Enumeration;

/**
 * 网络工具类，优先获取公司IP地址
 */
public class NetworkUtils {

    // 公司IP网段（可以配置多个）
    private static final String[] COMPANY_IP_PREFIXES = {
        "10.225.",     // 公司VPN网段
        "10.38.",      // 公司内网网段
        "172.16.",     // 其他可能的公司网段
        "192.168.100." // 其他可能的公司网段
    };

    /**
     * 获取本机IP地址，优先返回公司IP
     */
    public static InetAddress getLocalAddress() {
        InetAddress localAddress = null;
        InetAddress companyAddress = null;
        InetAddress fallbackAddress = null;

        try {
            // 首先尝试 getLocalHost()
            localAddress = InetAddress.getLocalHost();
            if (localAddress instanceof Inet6Address) {
                Inet6Address address = (Inet6Address) localAddress;
                if (isValidV6Address(address)) {
                    localAddress = normalizeV6Address(address);
                }
            }
            
            if (isCompanyAddress(localAddress)) {
                System.out.println("Found company IP from getLocalHost(): " + localAddress.getHostAddress());
                return localAddress;
            } else if (isValidAddress(localAddress)) {
                fallbackAddress = localAddress;
            }
        } catch (Throwable e) {
            System.out.println("getLocalHost() failed: " + e.getMessage());
        }

        try {
            // 遍历所有网络接口
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces == null) {
                return fallbackAddress != null ? fallbackAddress : localAddress;
            }

            while (interfaces.hasMoreElements()) {
                try {
                    NetworkInterface network = interfaces.nextElement();
                    if (!network.isUp() || network.isLoopback()) {
                        continue;
                    }

                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        try {
                            InetAddress address = addresses.nextElement();
                            
                            if (address instanceof Inet6Address) {
                                Inet6Address v6Address = (Inet6Address) address;
                                if (isValidV6Address(v6Address)) {
                                    address = normalizeV6Address(v6Address);
                                } else {
                                    continue;
                                }
                            }

                            if (isValidAddress(address)) {
                                // 检查是否是公司IP
                                if (isCompanyAddress(address)) {
                                    System.out.println("Found company IP from interface " + network.getName() + ": " + address.getHostAddress());
                                    companyAddress = address;
                                    // 如果找到公司IP，优先返回
                                    return companyAddress;
                                } else if (fallbackAddress == null) {
                                    fallbackAddress = address;
                                }
                            }
                        } catch (Throwable e) {
                            System.out.println("Error processing address: " + e.getMessage());
                        }
                    }
                } catch (Throwable e) {
                    System.out.println("Error processing network interface: " + e.getMessage());
                }
            }
        } catch (Throwable e) {
            System.out.println("Error getting network interfaces: " + e.getMessage());
        }

        // 优先级：公司IP > 其他有效IP > getLocalHost()结果
        if (companyAddress != null) {
            System.out.println("Returning company IP: " + companyAddress.getHostAddress());
            return companyAddress;
        } else if (fallbackAddress != null) {
            System.out.println("Returning fallback IP: " + fallbackAddress.getHostAddress());
            return fallbackAddress;
        } else {
            System.out.println("Returning localHost IP: " + (localAddress != null ? localAddress.getHostAddress() : "null"));
            return localAddress;
        }
    }

    /**
     * 检查是否是公司IP地址
     */
    private static boolean isCompanyAddress(InetAddress address) {
        if (address == null) {
            return false;
        }
        
        String ip = address.getHostAddress();
        for (String prefix : COMPANY_IP_PREFIXES) {
            if (ip.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否是有效的IPv4地址
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null) {
            return false;
        }

        if (address.isLoopbackAddress() || address.isAnyLocalAddress()) {
            return false;
        }

        if (address instanceof Inet4Address) {
            String ip = address.getHostAddress();
            // 排除无效的IP段
            if (ip.startsWith("169.254.") || // 链路本地地址
                ip.startsWith("0.") ||       // 无效地址
                ip.equals("255.255.255.255")) { // 广播地址
                return false;
            }
            return true;
        }

        return false;
    }

    /**
     * 检查是否是有效的IPv6地址
     */
    private static boolean isValidV6Address(Inet6Address address) {
        if (address == null) {
            return false;
        }

        if (address.isLoopbackAddress() || 
            address.isAnyLocalAddress() || 
            address.isLinkLocalAddress()) {
            return false;
        }

        return address.isSiteLocalAddress();
    }

    /**
     * 规范化IPv6地址
     */
    private static InetAddress normalizeV6Address(Inet6Address address) {
        return address;
    }

    /**
     * 获取本机IP地址字符串，优先返回公司IP
     */
    public static String getLocalIpAddress() {
        InetAddress address = getLocalAddress();
        return address != null ? address.getHostAddress() : "127.0.0.1";
    }
} 