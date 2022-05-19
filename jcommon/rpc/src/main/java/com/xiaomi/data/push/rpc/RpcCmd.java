package com.xiaomi.data.push.rpc;

/**
 * Created by zhangzhiyong on 05/06/2018.
 */
public abstract class RpcCmd {

    public static final int sfileReq = 0;
    public static final int sfileRes = 1;


    public static final int sfileReq2 = 10;
    public static final int sfileRes2 = 11;

    /**
     * ping
     */
    public static final int pingReq = 1001;
    public static final int pingRes = 1002;

    public static final int mpPingReq = 1003;
    public static final int mpPingRes = 1004;


    /**
     * 获取客户端信息
     */
    public static final int getInfoReq = 2000;
    public static final int getInfoRes = 2001;


}
