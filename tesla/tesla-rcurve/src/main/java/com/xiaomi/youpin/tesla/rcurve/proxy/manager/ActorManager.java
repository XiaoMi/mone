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

package com.xiaomi.youpin.tesla.rcurve.proxy.manager;

import akka.japi.pf.ReceiveBuilder;
import com.google.gson.Gson;
import com.xiaomi.mone.docean.plugin.akka.AkkaPlugin;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.bo.GrpcReqMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.bo.HttpReqMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.GRpcIngress;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.HttpIngress;

import javax.annotation.Resource;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/30 16:17
 */
@Service
public class ActorManager {

    @Resource
    private HttpIngress httpIngress;

    @Resource
    private GRpcIngress gRpcIngress;


    public void init() {
        AkkaPlugin akkaPlugin = Ioc.ins().getBean(AkkaPlugin.class);

        akkaPlugin.createActor("http_ingress", 500,
                ReceiveBuilder.create().match(HttpReqMsg.class, msg -> {
                    MeshResponse res = httpIngress.execute(msg.getCtx(), msg.getReq());
                    msg.getCtx().getHandlerContext().writeAndFlush(HttpResponseUtils.create(new Gson().toJson(res)));
                }).build());


        akkaPlugin.createActor("grpc_ingress", 500,
                ReceiveBuilder.create().match(GrpcReqMsg.class, msg -> {
                    MeshResponse res = gRpcIngress.execute(msg.getContext(), msg.getRequest());
                    msg.getFuture().complete(res);
                }).build());
    }

}
