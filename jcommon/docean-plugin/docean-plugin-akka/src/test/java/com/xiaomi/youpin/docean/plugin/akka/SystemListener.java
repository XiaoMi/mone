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
