package com.xiaomi.data.push.udp.po;

import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
public class UdpMsg {

    private String ip;
    private String message;


    public UdpMsg(String message, String ip) {
        this.ip = ip;
        this.message = message;
    }

}
