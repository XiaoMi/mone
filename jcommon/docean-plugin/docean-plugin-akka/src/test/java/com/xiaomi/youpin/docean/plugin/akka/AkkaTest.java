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
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import org.junit.Test;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/21 21:27
 */
public class AkkaTest {


    @Test
    public void testAkka() throws TimeoutException, InterruptedException, IOException {
        final ActorSystem system = ActorSystem.create("myakka");
        //创建akka actor 的成本非常低
        IntStream.range(0, 10000).forEach(i -> {
            ActorRef actor = system.actorOf(Props.create(MyActor.class, () -> new MyActor()), "myactor:" + i);
            System.out.println(actor);
        });
        ActorRef actor = system.actorOf(Props.create(MyActor.class, () -> new MyActor()), "myactor");

        actor.tell("go", ActorRef.noSender());
        //可以用路径选取
        system.actorSelection("/user/myactor").tell("hahah", ActorRef.noSender());

        Future<Object> f = Patterns.ask(actor, "hi", 1000);
        //通过await获取同步结果
        Object r = Await.result(f, Duration.create(1000, TimeUnit.MILLISECONDS));
        System.out.println(r);

        Future<Object> ask = Patterns.ask(actor, "zzy", 1000);
        //通过onComplete获取结果
        ask.onComplete(new OnComplete<Object>() {
            public void onComplete(Throwable throwable, Object o) throws Throwable {
                if (throwable != null) {
                    System.out.println("some thing wrong." + throwable);
                } else {
                    System.out.println("res:" + o);
                }
            }
        }, system.dispatcher());


        System.in.read();
    }


}
