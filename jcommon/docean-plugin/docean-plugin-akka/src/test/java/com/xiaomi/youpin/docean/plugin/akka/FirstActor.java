package com.xiaomi.youpin.docean.plugin.akka;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Author goodjava@qq.com
 * @Date 2021/2/1 10:33
 */
public class FirstActor extends AbstractActor {

    private int i = 0;

    private LoggingAdapter log = Logging.getLogger(this.getContext().system(),this);


    @Override
    public void preStart() throws Exception {
        this.getContext().setReceiveTimeout(Duration.create(3, TimeUnit.SECONDS));
    }

    @Override
    public Receive createReceive() {
        return ReceiveBuilder.create()
                .match(String.class, s -> {
                    if (s.equals("init")) {
                        log.info("init");
                        IntStream.range(0, 3000).forEach(i -> {
                            context().actorOf(Props.create(MyActor.class).withDispatcher("my-pinned-dispatcher"), "child:" + i);
                        });
                        log.info("create finish");
                    }

                    if (s.equals("msg")) {
                        log.info("msg");
                        this.getContext().getChildren().forEach(c -> {
                            c.tell(s, this.getSelf());
                        });
                    }

                    if (s.equals("add")) {
                        log.info("add:{}", i);
                        TimeUnit.SECONDS.sleep(10);
                        i++;
                    }

                })
                .match(ReceiveTimeout.class, msg -> {
                    log.info("timeout:{}", msg);
                })
                .build();

    }
}
