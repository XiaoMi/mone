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


import akka.actor.AbstractActor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/21 21:28
 */
@Slf4j
public class MyActor extends AbstractActor {


    @Override
    public void preStart() throws Exception, Exception {
        log.info("perStart");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, msg -> {
                    log.info("{} {}", msg, this.getSelf().path());
                    TimeUnit.SECONDS.sleep(3);
                    if (!msg.equals("msg")) {
                        getSender().tell(msg + "!!!", getSelf());
                    }
                })
                .build();
    }
}
