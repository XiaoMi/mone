package com.xiaomi.data.push.context;

import com.xiaomi.data.push.common.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author goodjava@qq.com
 */
@Component
public class ServerInfo {

    private String ip;

    @Value("${server.port}")
    private String port;

    public ServerInfo() {
        this.ip = Utils.getIp();
    }

    public String getIp() {
        return ip;
    }


    public String getPort() {
        return port;
    }


    @Override
    public String toString() {
        return this.ip + ":" + this.port;
    }
}
