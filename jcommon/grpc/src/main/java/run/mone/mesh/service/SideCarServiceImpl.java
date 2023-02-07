/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.mesh.service;

import com.google.protobuf.ByteString;
import com.xiaomi.data.push.uds.po.RpcCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.mone.grpc.context.GrpcServerContext;
import io.grpc.stub.StreamObserver;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import run.mone.mesh.bo.SideCarPushMsg;
import run.mone.mesh.bo.SideCarRequest;
import run.mone.mesh.bo.SideCarResponse;
import run.mone.mesh.bo.SideCarServiceGrpc;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

/**
 * @Author goodjava@qq.com
 * @Date 2022/6/22 10:50
 */
@Slf4j
@Data
public class SideCarServiceImpl extends SideCarServiceGrpc.SideCarServiceImplBase {


    private GrpcServerContext context;

    public SideCarServiceImpl() {
    }

    public SideCarServiceImpl(GrpcServerContext context) {
        this.context = context;
    }

    @Getter
    @Setter
    private ConcurrentHashMap<String, UdsProcessor<RpcCommand, RpcCommand>> processorMap = new ConcurrentHashMap<>();

    private BiConsumer<SideCarRequest,StreamObserver<SideCarResponse>> grpcConsumer;

    /**
     * 服务器处理调用进来的
     * 单次调用
     *
     * @param request
     * @param responseObserver
     */
    @Override
    public void call(SideCarRequest request, StreamObserver<SideCarResponse> responseObserver) {
        try {
            String grpc = request.getAttachmentsOrDefault("grpc","false");
            if (grpc.equals("true")) {
                grpcConsumer.accept(request,responseObserver);
                return;
            }
            byte[] data = request.getData().toByteArray();
            log.debug("sidcar call:{}", new String(data));
            String cmd = request.getCmd();
            UdsProcessor<RpcCommand, RpcCommand> processor = processorMap.get(cmd);
            RpcCommand command = new RpcCommand();
            command.setApp(request.getApp());
            command.setCmd(request.getCmd());
            command.setData(request.getData().toByteArray());
            RpcCommand rpcRes = processor.processRequest(command);
            byte[] d = new byte[]{};
            if (null != rpcRes) {
                d = rpcRes.data();
            }
            SideCarResponse res = SideCarResponse.newBuilder().setData(ByteString.copyFrom(d)).build();
            responseObserver.onNext(res);
            responseObserver.onCompleted();
        } catch (Throwable ex) {
            log.error(ex.getMessage(), ex);
            responseObserver.onError(ex);
        }
    }


    private ConcurrentHashMap<Integer, CompletableFuture> futureMap = new ConcurrentHashMap<>();


    private AtomicInteger reqId = new AtomicInteger();


    /**
     * 调用客户端(发送信息到sdiecar)
     *
     * @param request
     * @return
     */
    @SneakyThrows
    public SideCarRequest callSideCar(SideCarRequest request) {
        String app = request.getApp();
        StreamObserver<SideCarPushMsg> obs = context.getStreamMap().get(app);

        if (null == obs) {
            throw new RuntimeException("app:" + app + " is offline");
        }


        int id = reqId.incrementAndGet();
        SideCarPushMsg msg = SideCarPushMsg.newBuilder().setReqId(id)
                .setApp(request.getApp())
                .setCmd(request.getCmd())
                .setData(request.getData())
                .putAllAttachments(request.getAttachmentsMap())
                .setType("request").build();
        obs.onNext(msg);
        CompletableFuture<SideCarRequest> future = new CompletableFuture();
        futureMap.put(id, future);
        return future.get(3, TimeUnit.SECONDS);
    }


    @Override
    public StreamObserver<SideCarRequest> listen(StreamObserver<SideCarPushMsg> responseObserver) {
        return new StreamObserver<SideCarRequest>() {

            /**
             * 客户端调用过来的(或者返回的结果)
             * @param command
             */
            @Override
            public void onNext(SideCarRequest command) {

                if (command.getCmd().equals("connect")) {
                    context.getStreamMap().put(command.getApp(), responseObserver);
                }


                String type = command.getType();
                if (type.equals("request")) {
                    String cmd = command.getCmd();
                    RpcCommand rpcCommand = new RpcCommand();
                    RpcCommand res = SideCarServiceImpl.this.processorMap.get(cmd).processRequest(rpcCommand);
                    SideCarPushMsg pushMsg = SideCarPushMsg.newBuilder()
                            .setType("response")
                            .setData(ByteString.copyFrom(res.getData()))
                            .setReqId(command.getReqId())
                            .build();
                    responseObserver.onNext(pushMsg);

                } else {
                    //接收的response
                    Integer id = command.getReqId();
                    CompletableFuture future = futureMap.remove(id);
                    if (null != future) {
                        future.complete(command);
                    }
                }
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }

}
