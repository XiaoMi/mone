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

package com.xiaomi.youpin.tesla.rcurve.proxy.actor;

import akka.actor.AbstractActor;
import akka.japi.pf.ReceiveBuilder;
import com.google.gson.Gson;
import com.xiaomi.mone.grpc.demo.GrpcMeshResponse;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.GrpcReqMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.GRpcIngress;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/1 10:55
 */
public class GrpcIngressActor extends AbstractActor {


    private GRpcIngress gRpcIngress;

    public GrpcIngressActor(GRpcIngress gRpcIngress) {
        this.gRpcIngress = gRpcIngress;
    }

    @Override
    public Receive createReceive() {
        return  ReceiveBuilder.create().match(GrpcReqMsg.class, msg -> {
            MeshResponse res = gRpcIngress.execute(msg.getContext(), msg.getRequest());
            GrpcMeshResponse response = GrpcMeshResponse.newBuilder()
                    .setData(new Gson().toJson(res))
                    .build();
            msg.getResponseObserver().onNext(response);
            msg.getResponseObserver().onCompleted();
        }).build();
    }
}
