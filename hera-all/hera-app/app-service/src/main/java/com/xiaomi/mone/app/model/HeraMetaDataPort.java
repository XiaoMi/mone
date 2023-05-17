package com.xiaomi.mone.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/4/28 12:12 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeraMetaDataPort {

    private int port;

    private int dubboPort;

    private int httpPort;

    private int grpcPort;

    private int thriftPort;

}
