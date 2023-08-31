package com.xiaomi.mone.grpc;

import com.google.common.collect.Lists;
import com.google.protobuf.ByteString;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.common.GrpcServerConfig;
import com.xiaomi.mone.grpc.common.InitGrpcService;
import com.xiaomi.mone.grpc.context.GrpcServerContext;
import com.xiaomi.mone.grpc.server.filter.SideCarServerTransportFilter;
import com.xiaomi.mone.grpc.server.interceptor.SideCarServerInterceptor;
import com.xiaomi.mone.grpc.task.GrpcTask;
import com.xiaomi.mone.grpc.task.impl.PushTask;
import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.services.ProtoReflectionService;
import lombok.Data;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IServer;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.processor.server.ConnectProcessor;
import run.mone.mesh.processor.server.PingProcessor;
import run.mone.mesh.processor.server.SideCarProcessor;
import run.mone.mesh.service.SideCarServiceImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Data
@Slf4j
public class GrpcServer implements IServer<RpcCommand> {

    private int port = 8765;

    private Server server;

    @Getter
    private ConcurrentHashMap<String, UdsProcessor> processorMap = new ConcurrentHashMap<>();

    private GrpcServerContext context;

    @Getter
    private List<GrpcTask> taskList = new ArrayList<>();

    public GrpcServer(GrpcServerContext context) {
        this.context = context;
    }

    /**
     * 服务列表
     */
    private List<BindableService> serviceList;

    @Getter
    private SideCarServiceImpl sidecarService = new SideCarServiceImpl();


    private Runnable shutdowncallBack = () -> {
        log.info("shutdown");
        this.server.shutdown();
    };

    public void start() throws IOException, InterruptedException {
        ExecutorService executor = new ThreadPoolExecutor(GrpcServerConfig.GRPC_POOL_SIZE, GrpcServerConfig.GRPC_POOL_SIZE, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(GrpcServerConfig.GRPC_POOL_QUEUE_SIZE));
        ServerBuilder<?> builder = ServerBuilder
                .forPort(this.port)
                .executor(executor)
                .intercept(new SideCarServerInterceptor(context))
                .addTransportFilter(new SideCarServerTransportFilter(context));
        serviceList.stream().forEach(s -> builder.addService(s));
        builder.addService(ProtoReflectionService.newInstance());
        this.server = builder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(this.shutdowncallBack));
        server.start();
        initTask();
        log.info("grpc server start");
        server.awaitTermination();
    }

    private void initTask() {
        this.taskList.add(new PushTask());
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(this.taskList.size());
        this.taskList.forEach(t -> pool.scheduleAtFixedRate(() -> t.execute(), 0, 5, TimeUnit.SECONDS));
    }


    @SneakyThrows
    @Override
    public void start(String port) {
        InitGrpcService service = new InitGrpcService();
        service.init();
        sidecarService.setContext(context);
        //处理ping
        PingProcessor pingProcessor = new PingProcessor();
        sidecarService.getProcessorMap().put(pingProcessor.cmd(), pingProcessor);
        //处理链接
        ConnectProcessor connectProcessor = new ConnectProcessor();
        sidecarService.getProcessorMap().put(connectProcessor.cmd(), connectProcessor);
        //sidecar
        SideCarProcessor sideCarProcessor = new SideCarProcessor();
        sidecarService.getProcessorMap().put(sideCarProcessor.cmd(), sideCarProcessor);

        this.getProcessorMap().forEach((k, v) -> sidecarService.getProcessorMap().put(k, v));
        List<BindableService> serviceList = Lists.newArrayList(sidecarService);
        Optional.ofNullable(this.serviceList).ifPresent(list -> list.forEach(it -> serviceList.add(it)));

        this.setPort(Integer.valueOf(port));
        this.setServiceList(serviceList);
        this.start();
    }

    @Override
    public void putProcessor(UdsProcessor processor) {

    }


    @Override
    public RpcCommand call(RpcCommand req) {
        SideCarRequest request = SideCarRequest.newBuilder()
                .setReqId((int) req.getId())
                .setApp(req.getApp())
                .setData(ByteString.copyFrom(req.data()))
                .setCmd(req.getCmd())
                .putAllAttachments(req.getAttachments()).build();
        SideCarServiceImpl sideCarService = (SideCarServiceImpl) this.serviceList.get(0);
        SideCarRequest res = sideCarService.callSideCar(request);
        RpcCommand command = new RpcCommand();
        command.setData(res.getData().toByteArray());
        return command;
    }


}

