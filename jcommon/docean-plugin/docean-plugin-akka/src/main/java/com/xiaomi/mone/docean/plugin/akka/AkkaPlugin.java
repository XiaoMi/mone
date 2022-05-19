package com.xiaomi.mone.docean.plugin.akka;

import akka.actor.*;
import akka.routing.RoundRobinPool;
import akka.util.Timeout;
import com.google.common.base.Joiner;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.TimeUnit;
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

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init akka plugin");
        system = ActorSystem.create("akkaSystem");
        ioc.putBean(system);
    }

    public void createActor(String name, int num, AbstractActor.Receive receive) {
        log.info("create actor:{} {}", name, num);
        IntStream.range(0, num).mapToObj(i -> {
            ActorRef actor = system.actorOf(Props.create(DoceanActor.class, () -> new DoceanActor(receive)), Joiner.on("_").join(name, i));
            return actor;
        }).collect(Collectors.toList());
    }

    public void stopActor(String path, ActorContext context) {
        context.stop(system.actorSelection(path)
                .resolveOne(Timeout.apply(1, TimeUnit.SECONDS)).value().get().get());
    }

    public ActorRef createRoundRobinPool(ActorContext context, int num, String dispatcherName, String name, Class clazz, Object... parms) {
        ActorRef actor = context.actorOf(new RoundRobinPool(num)
                        .props(Props.create(clazz, parms))
                        .withDispatcher(dispatcherName),
                name);
        log.info("createRoundRobinPool num:{} name:{} path:{}", num, name, actor.path());
        return actor;
    }


    public static String getName(Object... params) {
        return Joiner.on(":").join(params);
    }
}
