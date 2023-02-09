package com.xiaomi.mone.grpc.server.filter.client;

import io.grpc.Attributes;
import io.grpc.Grpc;
import io.grpc.ServerTransportFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 1/3/21
 */
@Slf4j
public class SideCarClientTransportFilter extends ServerTransportFilter {

    @Override
    public Attributes transportReady(Attributes transportAttrs) {
        log.info("---->transportReady:{}", transportAttrs);
        return super.transportReady(transportAttrs);
    }



    @Override
    public void transportTerminated(Attributes transportAttrs) {
        log.info("---->transportTerminated:{}", transportAttrs);
        String remoteAddr = transportAttrs.get(Grpc.TRANSPORT_ATTR_REMOTE_ADDR).toString();
        super.transportTerminated(transportAttrs);
    }

}
