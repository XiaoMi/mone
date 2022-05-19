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
