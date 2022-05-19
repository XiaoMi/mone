package com.xiaomi.data.push.rpc.common;


import com.xiaomi.data.push.rpc.protocol.RemotingCommand;

public interface RPCHook {
    void doBeforeRequest(final String remoteAddr, final RemotingCommand request);


    void doAfterResponse(final String remoteAddr, final RemotingCommand request,
                         final RemotingCommand response);
}
