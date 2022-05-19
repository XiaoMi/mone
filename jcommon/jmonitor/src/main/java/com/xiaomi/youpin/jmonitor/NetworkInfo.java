package com.xiaomi.youpin.jmonitor;

/**
 * @author gaoyibo
 */
public class NetworkInfo {
    private String hostName;
    private String hostAddress;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public void setHostAddress(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    @Override
    public String toString() {
        return "NetworkInfo{" +
                "hostName='" + hostName + '\'' +
                ", hostAddress='" + hostAddress + '\'' +
                '}';
    }
}
