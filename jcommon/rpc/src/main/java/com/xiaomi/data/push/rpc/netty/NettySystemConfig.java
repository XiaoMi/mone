package com.xiaomi.data.push.rpc.netty;

public class NettySystemConfig {
    public static final String SystemPropertyNettyPooledByteBufAllocatorEnable =
            "com.rocketmq.remoting.nettyPooledByteBufAllocatorEnable";
    public static final String SystemPropertySocketSndbufSize = //
            "com.rocketmq.remoting.socket.sndbuf.size";
    public static final String SystemPropertySocketRcvbufSize = //
            "com.rocketmq.remoting.socket.rcvbuf.size";
    public static final String SystemPropertyClientAsyncSemaphoreValue = //
            "com.rocketmq.remoting.clientAsyncSemaphoreValue";
    public static final String SystemPropertyClientOnewaySemaphoreValue = //
            "com.rocketmq.remoting.clientOnewaySemaphoreValue";
    public static final boolean NettyPooledByteBufAllocatorEnable = //
            Boolean
                    .parseBoolean(System.getProperty(SystemPropertyNettyPooledByteBufAllocatorEnable, "false"));
    public static int socketSndbufSize = //
            Integer.parseInt(System.getProperty(SystemPropertySocketSndbufSize, "65535"));
    public static int socketRcvbufSize = //
            Integer.parseInt(System.getProperty(SystemPropertySocketRcvbufSize, "65535"));
    public static final int ClientAsyncSemaphoreValue = //
            Integer.parseInt(System.getProperty(SystemPropertyClientAsyncSemaphoreValue, "65535"));
    public static final int ClientOnewaySemaphoreValue = //
            Integer.parseInt(System.getProperty(SystemPropertyClientOnewaySemaphoreValue, "65535"));
}
