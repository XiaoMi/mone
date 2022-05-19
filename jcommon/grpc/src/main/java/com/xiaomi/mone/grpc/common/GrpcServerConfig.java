package com.xiaomi.mone.grpc.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 */
@Data
@Builder
public class GrpcServerConfig {

    private int port;

    public static int GRPC_POOL_SIZE = 200;

    public static int GRPC_POOL_QUEUE_SIZE = 1000;
}
