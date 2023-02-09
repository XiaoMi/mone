package com.xiaomi.mone.grpc;

import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import lombok.Getter;
import run.mone.api.IClient;
import run.mone.mesh.bo.SideCarRequest;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author goodjava@qq.com
 * @date 2022/7/4 15:37
 */
public class GrpcClientGroup implements IClient<RpcCommand> {

    @Getter
    private ConcurrentHashMap<String,GrpcClient> clients = new ConcurrentHashMap<>();


    @Override
    public void start(String str) {

    }

    @Override
    public RpcCommand call(RpcCommand rpcCommand) {
        return null;
    }

    @Override
    public Object callServer(Object request) {
        SideCarRequest req = (SideCarRequest) request;
        GrpcClient c = clients.get(req.getApp());
        return c.callServer(req);
    }

    @Override
    public ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> getProcessorMap() {
        return null;
    }
}
