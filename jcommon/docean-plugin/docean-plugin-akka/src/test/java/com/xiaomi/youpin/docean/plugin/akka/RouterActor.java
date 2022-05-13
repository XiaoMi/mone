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

package com.xiaomi.youpin.docean.plugin.akka;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import akka.routing.RoundRobinPool;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/7 18:07
 */
public class RouterActor extends UntypedAbstractActor {

    private ActorRef router;


    @Override
    public void preStart() throws Exception, Exception {
//        String dispatcher = "my-pinned-dispatcher";
        String dispatcher = "my-forkjoin-dispatcher";
        router  = getContext().actorOf(new RoundRobinPool(200).props(
                Props.create(MyActor.class).withDispatcher(dispatcher)
                )
                ,"myactor");
    }

    @Override
    public void onReceive(Object message) throws Throwable, Throwable {
        router.tell(message, getSender());
    }
}
