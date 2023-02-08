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
import akka.actor.DeadLetter;
import akka.japi.pf.ReceiveBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/5 11:08
 */
@Slf4j
public class SystemListener extends AbstractActor {
    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(DeadLetter.class, msg -> {
                    log.info("dead letter:{}", msg);
                })
                .build();
    }
}
