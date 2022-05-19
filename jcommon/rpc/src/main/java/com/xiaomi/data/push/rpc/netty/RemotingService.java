package com.xiaomi.data.push.rpc.netty;

import com.xiaomi.data.push.rpc.common.RPCHook;

public interface RemotingService {
    void start();


    void shutdown();


    void registerRPCHook(RPCHook rpcHook);
}
