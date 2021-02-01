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

package com.xiaomi.youpin.tesla.rcurve.proxy.actor.message;

import com.xiaomi.mone.grpc.demo.GrpcMeshRequest;
import com.xiaomi.mone.grpc.demo.GrpcMeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.context.ProxyContext;
import io.grpc.stub.StreamObserver;
import lombok.Builder;
import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/30 15:55
 */
@Data
@Builder
public class GrpcReqMsg {

    private ProxyContext context;

    private GrpcMeshRequest request;

    private StreamObserver<GrpcMeshResponse> responseObserver;

}
