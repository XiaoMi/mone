package com.xiaomi.mone.grpc;

import com.google.protobuf.ByteString;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.demo.*;
import com.xiaomi.mone.grpc.observer.client.PushMsgObserver;
import com.xiaomi.mone.grpc.server.interceptor.client.SideCarClientInterceptor;
import com.xiaomi.mone.grpc.task.GrpcTask;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.api.IClient;
import run.mone.mesh.bo.*;
import run.mone.mesh.obs.ClientObs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Slf4j
public class GrpcClient implements IClient<RpcCommand> {

    @Setter
    @Getter
    private ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> processorMap = new ConcurrentHashMap<>();

    @Setter
    private SideCarAddress address;

    @Getter
    @Setter
    private String app;

    private SideCarServiceGrpc.SideCarServiceStub stub;

    private Channel ch;

    private SideCarServiceGrpc.SideCarServiceBlockingStub sideCarServiceBlockingStub;

    private MeshServiceGrpc.MeshServiceBlockingStub meshServiceBlockingStub;

    private MeshServiceGrpc.MeshServiceStub meshServiceStub;

    @Getter
    private List<GrpcTask> taskList = new ArrayList<>();


    /**
     * 发送消息到服务端
     *
     * @param request
     * @return
     */
    public GrpcMeshResponse call(GrpcMeshRequest request) {
        GrpcMeshResponse response = meshServiceBlockingStub.call(request);
        return response;
    }


    public SideCarResponse call(SideCarRequest request) {
        SideCarResponse response = this.sideCarServiceBlockingStub.call(request);
        return response;
    }


    /**
     * 监听server推回来的信息
     *
     * @param request
     * @throws InterruptedException
     */
    public void listen(GrpcMeshRequest request) throws InterruptedException {
        while (true) {
            CountDownLatch latch = new CountDownLatch(1);
            meshServiceStub.listen(request, new StreamObserver<PushMsg>() {
                @Override
                public void onNext(PushMsg value) {
                    System.out.println(value);
                }

                @Override
                public void onError(Throwable t) {
                    log.info("listen error:" + t.getMessage());
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    log.info("listen completed");
                    latch.countDown();
                }
            });
            latch.await();
            TimeUnit.SECONDS.sleep(5);
        }
    }


    public void listen(BiConsumer<SideCarPushMsg, StreamObserver<SideCarRequest>> consumer, boolean reconnect) throws InterruptedException {
        while (true) {
            log.info("create listen");
            CountDownLatch latch = new CountDownLatch(1);
            //服务端过来的请求
            PushMsgObserver pushMsgObs = new PushMsgObserver(consumer, latch);
            StreamObserver<SideCarRequest> obs = stub.listen(pushMsgObs);
            SideCarRequest connect = SideCarRequest.newBuilder().setType("request").setApp(app).setCmd("connect").build();
            obs.onNext(connect);
            pushMsgObs.setObs(obs);
            latch.await();
            if (!reconnect) {
                break;
            }
            TimeUnit.SECONDS.sleep(5);
        }
    }


    public void shutdown() {
    }


    /**
     * 启动客户端
     *
     * @param str
     */
    @SneakyThrows
    @Override
    public void start(String str) {
        String[] params = str.split(":");
        log.info("params:{}", Arrays.toString(params));
        this.app = params[2];
        boolean listen = true;
        if (params.length > 3) {
            listen = false;
        }
        this.address = SideCarAddress.newBuilder()
                .setIp(params[0])
                .setPort(Integer.valueOf(params[1]))
                .setApp(this.app)
                .build();
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(address.getIp(), address.getPort())
                .usePlaintext()
                .build();
        this.ch = ClientInterceptors.intercept(channel, new SideCarClientInterceptor(address));
        this.stub = SideCarServiceGrpc.newStub(ch);
        this.sideCarServiceBlockingStub = SideCarServiceGrpc.newBlockingStub(ch);
        this.meshServiceBlockingStub = MeshServiceGrpc.newBlockingStub(ch);
        this.meshServiceStub = MeshServiceGrpc.newStub(ch);
        ScheduledExecutorService pool = Executors.newScheduledThreadPool(this.taskList.size());
        this.taskList.forEach(t -> pool.scheduleAtFixedRate(() -> t.execute(), 0, 5, TimeUnit.SECONDS));
        if (listen) {
            new Thread(() -> {
                try {
                    this.listen(new ClientObs(this.getProcessorMap()), true);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @Override
    public RpcCommand call(RpcCommand rpcCommand) {
        SideCarRequest req = SideCarRequest.newBuilder().setApp(rpcCommand.getApp())
                .setCmd(rpcCommand.getCmd())
                .setData(ByteString.copyFrom(rpcCommand.data()))
                .build();
        SideCarResponse res = this.call(req);
        RpcCommand rpcRes = new RpcCommand();
        rpcRes.setData(res.getData().toByteArray());
        return rpcRes;
    }


    public void start(MeshAddress address) {
        String str = address.getIp() + ":" + address.getPort();
        start(str);
    }


    @Override
    public Object callServer(Object request) {
        SideCarRequest req = (SideCarRequest) request;
        return this.call(req);
    }

}
