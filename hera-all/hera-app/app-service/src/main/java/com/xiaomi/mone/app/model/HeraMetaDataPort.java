package com.xiaomi.mone.app.model;

import lombok.Data;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/28 12:12 PM
 */
@Data
public class HeraMetaDataPort {

    private int port;

    private int dubboPort;

    private int httpPort;

    private int grpcPort;

    private int thriftPort;

    public HeraMetaDataPort(){}

    public HeraMetaDataPort(int port, int dubboPort, int httpPort, int grpcPort, int thriftPort) {
        this.port = port;
        this.dubboPort = dubboPort;
        this.httpPort = httpPort;
        this.grpcPort = grpcPort;
        this.thriftPort = thriftPort;
    }
}
