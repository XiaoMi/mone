package com.xiaomi.mone.grpc.service;

import com.google.gson.Gson;
import com.xiaomi.mone.grpc.demo.GrpcMeshRequest;
import com.xiaomi.mone.grpc.demo.GrpcMeshResponse;
import com.xiaomi.mone.grpc.demo.MeshServiceGrpc;
import com.xiaomi.mone.grpc.demo.PushMsg;
import io.grpc.stub.StreamObserver;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 1/2/21
 */
@Slf4j
public class MeshServiceImpl extends MeshServiceGrpc.MeshServiceImplBase {

    private Gson gson = new Gson();

    @Setter
    private BiFunction<GrpcMeshRequest, StreamObserver<GrpcMeshResponse>, Object> invoker;

    public static ConcurrentHashMap<String, StreamObserver<PushMsg>> queueMap = new ConcurrentHashMap<>();

    @Override
    public void listen(GrpcMeshRequest request, StreamObserver<PushMsg> responseObserver) {
        queueMap.put(request.getId(), responseObserver);
    }

    @SneakyThrows
    @Override
    public void call(GrpcMeshRequest request, StreamObserver<GrpcMeshResponse> responseObserver) {
        Object res = invoker.apply(request,responseObserver);

        //akka 哪里异步处理掉了
        if (null == res) {
            return;
        }

        if (res instanceof CompletableFuture) {
            CompletableFuture future = (CompletableFuture) res;
            res = future.get();
        }

        GrpcMeshResponse response = GrpcMeshResponse.newBuilder()
                .setData(gson.toJson(res))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
