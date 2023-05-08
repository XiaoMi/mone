package com.xiaomi.mone.app.api.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/28 12:12 PM
 */
@Data
@ToString
public class HeraMetaDataPortModel implements Serializable {

    private int port;

    private int dubboPort;

    private int httpPort;

    private int grpcPort;

    private int thriftPort;

    public HeraMetaDataPortModel(){}

    public HeraMetaDataPortModel(int port, int dubboPort, int httpPort, int grpcPort, int thriftPort) {
        this.port = port;
        this.dubboPort = dubboPort;
        this.httpPort = httpPort;
        this.grpcPort = grpcPort;
        this.thriftPort = thriftPort;
    }
}
