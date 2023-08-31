package com.xiaomi.mone.app.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/28 12:12 PM
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HeraMetaDataPortModel implements Serializable {

    private int port;

    private int dubboPort;

    private int httpPort;

    private int grpcPort;

    private int thriftPort;
}
