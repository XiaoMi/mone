package com.xiaomi.youpin.tesla.rcurve.proxy.ingress;

import com.google.common.collect.Lists;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.mone.docean.plugin.akka.AkkaPlugin;
import com.xiaomi.mone.grpc.GrpcServer;
import com.xiaomi.mone.grpc.demo.GrpcMeshRequest;
import com.xiaomi.mone.grpc.service.MeshServiceImpl;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.Safe;
import com.xiaomi.youpin.docean.plugin.config.anno.Value;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.Proxy;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.GrpcReqMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyType;
import io.grpc.BindableService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 * Grpc Ingress
 */
@Component
@Slf4j
public class GRpcIngress implements Proxy<GrpcMeshRequest, MeshResponse> {

    @Resource
    private UdsServer udsServer;

    @Value("$grpcPort")
    private String grpcPort;

    @Value(value = "$openGrpc", defaultValue = "false")
    private String openGrpc;


    @Value(value = "$use_actor", defaultValue = "false")
    private String useActor;


    public void init() {
        log.info("grpc proxy init");
        if (openGrpc.equals("false")) {
            return;
        }
        AkkaPlugin akkaPlugin = Ioc.ins().getBean(AkkaPlugin.class);
        GrpcServer grpcServer = new GrpcServer();
        MeshServiceImpl meshService = new MeshServiceImpl();
        ProxyContext context = new ProxyContext();
        context.setType(ProxyType.grpc);
        meshService.setInvoker((grpcRequest, observer) -> {
            if (useActor.equals("true")) {
                akkaPlugin.sendMessage(AkkaPlugin.getName("grpc", grpcRequest.getApp()), GrpcReqMsg.builder()
                        .context(context)
                        .responseObserver(observer)
                        .request(grpcRequest).build());
                //返回空,grpc的线程就不会等待了
                return null;
            }
            return execute(context, grpcRequest);
        });
        List<BindableService> serviceList = Lists.newArrayList(meshService);
        grpcServer.setServiceList(serviceList);
        grpcServer.setPort(Integer.valueOf(grpcPort));
        new Thread(() -> Safe.run(() -> grpcServer.start())).start();

    }

    @Override
    public MeshResponse execute(ProxyContext context, GrpcMeshRequest request) {
        log.info("grpc execute");
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
        command.setByteParams(Arrays.stream(request.getParamsList().stream().toArray(String[]::new)).map(it -> it.getBytes()).toArray(byte[][]::new));
        command.setTimeout(request.getTimeout());
        command.putAtt("resultJson", "true");
        try {
            UdsCommand res = udsServer.call(command);
            MeshResponse meshResponse = new MeshResponse();
            meshResponse.setCode(res.getCode());
            meshResponse.setMessage(res.getMessage());
            String data = new String((byte[]) res.getData(byte[].class));
            meshResponse.setData(data);
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
