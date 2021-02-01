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

import akka.actor.ActorRef;
import akka.actor.Props;
import com.xiaomi.mone.docean.plugin.akka.AkkaPlugin;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.AppActor;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.RegAppMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.actor.message.UnRegAppMsg;
import com.xiaomi.youpin.tesla.rcurve.proxy.ingress.DubboIngress;
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

    @Resource
    private DubboIngress dubboIngress;


    private AkkaPlugin akkaPlugin;


    public void init() {
        akkaPlugin = Ioc.ins().getBean(AkkaPlugin.class);
        ActorRef actor = akkaPlugin.getSystem().actorOf(Props.create(AppActor.class, () ->
                new AppActor(httpIngress, gRpcIngress, dubboIngress)), "appActor");
        RegAppMsg msg = new RegAppMsg();
        msg.setAppName("default");
        actor.tell(msg, ActorRef.noSender());
    }


    public void regApp(String appName) {
        RegAppMsg msg = new RegAppMsg();
        msg.setAppName(appName);
        akkaPlugin.getSystem().actorSelection("/user/appActor").tell(msg, ActorRef.noSender());
    }


    public void unregApp(String appName) {
        UnRegAppMsg msg = new UnRegAppMsg();
        msg.setAppName(appName);
        akkaPlugin.getSystem().actorSelection("/user/appActor").tell(msg, ActorRef.noSender());
    }

}
