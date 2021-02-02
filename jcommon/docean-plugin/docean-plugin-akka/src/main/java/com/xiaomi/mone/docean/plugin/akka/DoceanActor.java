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

package com.xiaomi.mone.docean.plugin.akka;

import akka.actor.AbstractActor;

/**
 * @author goodjava@qq.com
 * @date 1/30/21
 */
public class DoceanActor extends AbstractActor {

    private Receive receive;

    public DoceanActor(Receive receive) {
        this.receive = receive;
    }

    @Override
    public Receive createReceive() {
        return this.receive;
    }
}
