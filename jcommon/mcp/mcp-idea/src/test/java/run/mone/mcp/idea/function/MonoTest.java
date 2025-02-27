package run.mone.mcp.idea.function;

import lombok.SneakyThrows;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/2/20 15:45
 */
public class MonoTest {


    @Test
    public void test0() {

    }


    @SneakyThrows
    @Test
    public void test1() {
        Mono<String> mono = Mono.just("abc");

        mono.subscribe(str->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(str);
        });

        CountDownLatch latch = new CountDownLatch(1);
        mono.subscribe(new Subscriber<String>() {

            @Override
            public void onSubscribe(Subscription subscription) {
                subscription.request(1);
            }

            @SneakyThrows
            @Override
            public void onNext(String s) {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("ssss:"+s);
                latch.countDown();
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });

        latch.await();

        System.out.println("finish");

    }

    @Test
    public void test2() throws InterruptedException {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> flux = sink.asFlux();

        flux.subscribe(value->System.out.println(value),error->System.out.println(error),()->System.out.println("Completed"));

        new Thread(()->{
            sink.emitNext("gogogo",Sinks.EmitFailureHandler.FAIL_FAST);
        }).start();

        Thread.sleep(1000);
    }

}
