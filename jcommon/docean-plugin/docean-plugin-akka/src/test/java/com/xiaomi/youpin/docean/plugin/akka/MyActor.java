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
