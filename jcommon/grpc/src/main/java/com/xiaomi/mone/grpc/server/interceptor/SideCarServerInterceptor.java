package com.xiaomi.mone.grpc.server.interceptor;

import com.xiaomi.mone.grpc.context.GrpcServerContext;
import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import run.mone.mesh.common.Cons;

import java.util.Set;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class SideCarServerInterceptor implements ServerInterceptor {


    private GrpcServerContext context;


    public SideCarServerInterceptor(GrpcServerContext context) {
        this.context = context;
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {

        log.info("headers:{}", headers);

        headers.keys().forEach(it -> {
            String v = headers.get(Metadata.Key.of(it, Metadata.ASCII_STRING_MARSHALLER));
            log.debug("key:{},value:{}", it, v);
        });
        String app = headers.get(Cons.SIDE_CAR_APP);
        if (!StringUtils.isEmpty(app)) {
            String remoteAddr = call.getAttributes().get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString();
            log.debug("side car app:{} addr:{}", app, remoteAddr);
            context.put(app, remoteAddr);

            Set<Attributes.Key<?>> keys = call.getAttributes().keys();
            keys.stream().forEach(it -> {
                Object value = call.getAttributes().get(it);
                log.info("key:{} = value:{}", it, value);
            });
        }

        return new ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT>(next.startCall(call, headers)) {
            @Override
            public void onMessage(ReqT message) {
                super.onMessage(message);
                log.debug("on message:{}", message);
            }
        };
    }
}
