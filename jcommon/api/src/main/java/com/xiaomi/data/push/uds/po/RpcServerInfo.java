package com.xiaomi.data.push.uds.po;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/2/27 15:49
 */
@Data
public class RpcServerInfo implements Serializable {

    private String host;

    private int port;

    private String name;

    private boolean reg;
}
