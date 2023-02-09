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
import com.xiaomi.youpin.gateway.common.HttpResponseUtils;
import com.xiaomi.youpin.tesla.proxy.MeshResponse;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.HttpReqMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.HttpIngress;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/1 10:54
 */
@Slf4j
public class HttpIngressActor extends AbstractActor {


    private HttpIngress httpIngress;


    public HttpIngressActor(HttpIngress httpIngress) {
        this.httpIngress = httpIngress;
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create().match(HttpReqMsg.class, msg -> {
            log.debug("receive msg:{}", msg);
            MeshResponse res = httpIngress.execute(msg.getCtx(), msg.getReq());
            msg.getCtx().getHandlerContext().writeAndFlush(HttpResponseUtils.create(new Gson().toJson(res)));
        }).build();
    }
}
