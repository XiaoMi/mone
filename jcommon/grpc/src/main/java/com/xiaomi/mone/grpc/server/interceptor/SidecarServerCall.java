package com.xiaomi.mone.grpc.server.interceptor;

import io.grpc.ForwardingServerCall;
import io.grpc.MethodDescriptor;
import io.grpc.ServerCall;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangping17
 * @param <ReqT>
 * @param <RespT>
 */
@Slf4j
public class SidecarServerCall<ReqT,RespT> extends ForwardingServerCall.SimpleForwardingServerCall<ReqT,RespT> {

    protected SidecarServerCall(ServerCall<ReqT, RespT> delegate) {
        super(delegate);
    }

    @Override
    protected ServerCall<ReqT, RespT> delegate() {
        return super.delegate();
    }

    @Override
    public MethodDescriptor<ReqT, RespT> getMethodDescriptor() {
        return super.getMethodDescriptor();
    }


    @Override
    public void sendMessage(RespT message) {
        log.info("grpc server response:{}", message);
        super.sendMessage(message);
    }
}
