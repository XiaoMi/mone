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
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.google.common.base.Joiner;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 20:31
 */
@DOceanPlugin
@Slf4j
public class AkkaPlugin implements IPlugin {

    @Getter
    private ActorSystem system;

    private ConcurrentHashMap<String, ActorInfo> map = new ConcurrentHashMap<>();

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init akka plugin");
        system = ActorSystem.create("akkaSystem");
        ioc.putBean(system);
    }

    public void createActor(String name, int num, AbstractActor.Receive receive) {
        log.info("create actor:{} {}", name, num);
        ActorInfo info = new ActorInfo();
        info.setName(name);
        info.setNum(num);
        List<ActorRef> list = IntStream.range(0, num).mapToObj(i -> {
            ActorRef actor = system.actorOf(Props.create(DoceanActor.class, () -> new DoceanActor(receive)), Joiner.on("_").join(name, i));
            return actor;
        }).collect(Collectors.toList());
        info.setList(list);
        map.put(name, info);
    }


    public void createActor(String name, int num, List<ActorRef> list) {
        log.info("create actor:{} {}", name, num);
        ActorInfo info = new ActorInfo();
        info.setName(name);
        info.setNum(num);
        info.setList(list);
        map.put(name, info);
    }


    public void destoryActor(String name) {
        log.info("destroy actor:{}", name);
        map.computeIfPresent(name, (k, v) -> {
            v.getList().forEach(it -> {
                this.system.stop(it);
            });
            return null;
        });
    }

    public void sendMessage(String name, Object msg) {
        ActorInfo info = map.get(name);
        int num = info.getNum();
        Random random = new Random();
        int v = random.nextInt(num);
        ActorRef actorRef = info.getList().get(v);
        actorRef.tell(msg, ActorRef.noSender());
    }

    public static String getName(Object... params) {
        return Joiner.on(":").join(params);
    }
}
