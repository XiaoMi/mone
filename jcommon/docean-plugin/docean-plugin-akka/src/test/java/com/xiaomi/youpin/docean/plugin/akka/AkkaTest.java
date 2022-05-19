package com.xiaomi.youpin.docean.plugin.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.DeadLetter;
import akka.actor.Props;
import akka.dispatch.MessageDispatcher;
import akka.dispatch.OnComplete;
import akka.pattern.Patterns;
import com.typesafe.config.ConfigFactory;
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
    public void testTimeout() throws IOException {
        ActorSystem system = ActorSystem.create("myakka");
        ActorRef actor = system.actorOf(Props.create(FirstActor.class), "first");
        actor.tell("init", ActorRef.noSender());
        actor.tell("msg", ActorRef.noSender());
        actor.tell("add", ActorRef.noSender());
        System.in.read();
    }


    @Test
    public void testCreateActor() throws IOException {
        ActorSystem system = ActorSystem.create("myakka");
        ActorRef actor = system.actorOf(Props.create(FirstActor.class), "first");
        actor.tell("init", ActorRef.noSender());
        actor.tell("msg", ActorRef.noSender());
        System.in.read();
    }


    @Test
    public void testDispatcher() throws IOException {
        ActorSystem system = ActorSystem.create("myakka");
//        String dispatcherName = "my-pinned-dispatcher";
        String dispatcherName = "my-threadpool.dispatcher";
        String dispatcherName2 = "my-threadpool.dispatcher.2";

        ActorRef actorRef = system.actorOf(Props.create(MyActor.class).withDispatcher(dispatcherName));
        ActorRef actorRef2 = system.actorOf(Props.create(MyActor.class).withDispatcher(dispatcherName2));

        actorRef.tell("msg", ActorRef.noSender());
        actorRef2.tell("msg", ActorRef.noSender());
        System.out.println("send finish");
        System.in.read();
    }


    @Test
    public void testRouter() throws IOException {
        ActorSystem system = ActorSystem.create("myakka");
        ActorRef actorRef = system.actorOf(Props.create(RouterActor.class));
        IntStream.range(0, 30).forEach(i -> {
            actorRef.tell("msg", ActorRef.noSender());
        });
        System.out.println("send finish");
        System.in.read();
    }


    @Test
    public void testMailBox() throws IOException {
        ActorSystem system = ActorSystem.create("myakka");

        ActorRef systemListener = system.actorOf(Props.create(SystemListener.class), "systemListener");
        system.eventStream().subscribe(systemListener, DeadLetter.class);

        ActorRef actor = system.actorOf(Props.create(FirstActor.class).withMailbox("bounded-mailbox"), "first");
        IntStream.range(0, 120000).parallel().forEach(i -> {
            actor.tell("add", ActorRef.noSender());
        });
        System.in.read();
    }


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
