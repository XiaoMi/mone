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
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.DubboReqMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.DubboIngress;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/1 10:55
 */
@Slf4j
public class DubboIngressActor extends AbstractActor {

    private String name;

    private DubboIngress dubboIngress;

    public DubboIngressActor(String name, DubboIngress dubboIngress) {
        this.name = name;
        this.dubboIngress = dubboIngress;
    }

    @Override
    public void preStart() throws Exception, Exception {
        log.debug("pre start dubbo ingress actor:{}", name);
    }


    @Override
    public void postStop() throws Exception, Exception {
        log.debug("post stop dubbo ingress actor:{}", name);
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(DubboReqMsg.class, msg -> {
                    MeshResponse res = dubboIngress.invoke(msg.getMeshRequest());
                    msg.getFuture().complete(res);
                })
                .build();
    }


}
