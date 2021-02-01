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
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.xiaomi.mone.docean.plugin.akka.AkkaPlugin;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.RegAppMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.UnRegAppMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.DubboIngress;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.GRpcIngress;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.HttpIngress;

import java.util.List;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/1 10:48
 */
public class AppActor extends AbstractActor {

    private HttpIngress httpIngress;

    private GRpcIngress gRpcIngress;

    private DubboIngress dubboIngress;

    private int num = 100;

    public AppActor(HttpIngress httpIngress, GRpcIngress gRpcIngress, DubboIngress dubboIngress) {
        this.httpIngress = httpIngress;
        this.gRpcIngress = gRpcIngress;
        this.dubboIngress = dubboIngress;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(RegAppMsg.class, msg -> {
                    List<ActorRef> httpList = Lists.newArrayList();
                    List<ActorRef> dubboList = Lists.newArrayList();
                    List<ActorRef> grpcList = Lists.newArrayList();
                    IntStream.range(0, num).forEach(i -> {
                        httpList.add(context().actorOf(Props.create(HttpIngressActor.class, httpIngress), Joiner.on(":").join("http", msg.getAppName(), i)));
                        grpcList.add(context().actorOf(Props.create(GrpcIngressActor.class, gRpcIngress), Joiner.on(":").join("grpc", msg.getAppName(), i)));
                        String name = Joiner.on(":").join("dubbo", msg.getAppName(), i);
                        dubboList.add(context().actorOf(Props.create(DubboIngressActor.class, name, dubboIngress), name));
                    });

                    AkkaPlugin akka = Ioc.ins().getBean(AkkaPlugin.class);
                    akka.createActor(Joiner.on(":").join("http", msg.getAppName()), num, httpList);
                    akka.createActor(Joiner.on(":").join("grpc", msg.getAppName()), num, grpcList);
                    akka.createActor(Joiner.on(":").join("dubbo", msg.getAppName()), num, dubboList);

                })
                .match(UnRegAppMsg.class, msg -> {
                    AkkaPlugin akka = Ioc.ins().getBean(AkkaPlugin.class);
                    akka.destoryActor(Joiner.on(":").join("http", msg.getAppName()));
                    akka.destoryActor(Joiner.on(":").join("grpc", msg.getAppName()));
                    akka.destoryActor(Joiner.on(":").join("dubbo", msg.getAppName()));
                })
                .build();
    }
}
