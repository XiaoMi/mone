package com.xiaomi.youpin.tesla.rcurve.proxy.impl;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.mone.grpc.GrpcServer;
import com.xiaomi.mone.grpc.demo.GrpcMeshRequest;
import com.xiaomi.mone.grpc.service.MeshServiceImpl;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.tesla.rcurve.proxy.Proxy;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyType;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import io.grpc.BindableService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 * grpc 代理协议的支持
 */
@Component
@Slf4j
public class GRpcProxy implements Proxy<GrpcMeshRequest, MeshResponse> {

    @Resource
    private UdsServer udsServer;

    @Value("$grpcPort")
    private String grpcPort;

    @Value(value = "$openGrpc", defaultValue = "false")
    private String openGrpc;

    public void init() {
        log.info("grpc proxy init");
        if (openGrpc.equals("false")) {
            return;
        }
        GrpcServer grpcServer = new GrpcServer();
        MeshServiceImpl meshService = new MeshServiceImpl();
        ProxyContext context = new ProxyContext();
        context.setType(ProxyType.grpc);
        meshService.setInvoker(grpcRequest -> execute(context, grpcRequest));
        List<BindableService> serviceList = Lists.newArrayList(meshService);
        grpcServer.setServiceList(serviceList);
        grpcServer.setPort(Integer.valueOf(grpcPort));
        new Thread(() -> Safe.run(() -> grpcServer.start())).start();
    }

    @Override
    public MeshResponse execute(ProxyContext context, GrpcMeshRequest request) {
        if (request.getMethodName().equals("$version$")) {
            return new MeshResponse(this.type() + ":" + this.version());
        }
        UdsCommand command = UdsCommand.createRequest();
        command.setCmd("call");
        command.setApp(request.getApp());
        command.setServiceName(request.getServiceName());
        command.setMethodName(request.getMethodName());
        command.setParamTypes(request.getParamTypesList().stream().toArray(String[]::new));
        command.setParams(request.getParamsList().stream().toArray(String[]::new));
        command.setTimeout(request.getTimeout());
        try {
            UdsCommand res = udsServer.call(command);
            MeshResponse meshResponse = new MeshResponse();
            meshResponse.setCode(res.getCode());
            meshResponse.setMessage(res.getMessage());
            meshResponse.setData(res.getData());
            return meshResponse;
        } catch (Throwable ex) {
            MeshResponse res = new MeshResponse();
            res.setCode(500);
            res.setMessage(ex.getMessage());
            return res;
        }
    }

    @Override
    public String type() {
        return ProxyType.grpc.name();
    }
}
