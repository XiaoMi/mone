package run.mone.mcp.idea.function;

import lombok.SneakyThrows;
import org.junit.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2025/2/20 15:45
 */
public class MonoTest {


    @Test
    public void test0() {
        Flux<String> flux = Flux.create(emmiter -> {
            emmiter.onDispose(()->{
                System.out.println("dispose");
            });

            IntStream.range(0, 3).forEach(it -> {
                emmiter.next(String.valueOf(it));
            });
            emmiter.complete();
        });

        flux.subscribe(it -> {
                    System.out.println(it);
                },
                error -> System.out.println(error),
                () -> System.out.println("completed")

        );
        System.out.println("finish");
    }


    @SneakyThrows
    @Test
    public void test1() {
        Mono<String> mono = Mono.just("abc");

        mono.subscribe(str -> {
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
                System.out.println("ssss:" + s);
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

        flux.subscribe(value -> System.out.println(value), error -> System.out.println(error), () -> System.out.println("Completed"));

        new Thread(() -> {
            sink.emitNext("gogogo", Sinks.EmitFailureHandler.FAIL_FAST);
        }).start();

        Thread.sleep(1000);
    }


    @Test
    public void test3() {
        Flux.just("a","b","c").doOnNext(it->{
            System.out.println(it);
        }).subscribe();
    }

    @Test
    public void test4() {
        Mono<String> mono = Flux.just("a", "b").next();
        System.out.println(mono.block());
        System.out.println(mono.block());
    }

    @Test
    public void test5() {
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        sink.tryEmitNext("a1");
        sink.tryEmitNext("b1");
        sink.tryEmitComplete();
        Flux<String> flux = sink.asFlux();
        flux.subscribe(it->{
            System.out.println(it);
        });
    }

    @Test
    public void test6() {
        Mono<String> m = Mono.just("a");
        Mono<String> m2 = Mono.just("b");
        String str = Mono.zip(m,m2).map(it->{
            return it.getT1()+":"+it.getT2();
        }).block();
        System.out.println(str);
    }

    @Test
    public void test7() {
        Flux<String> flux = Flux.just("a","b","c");
        Mono<List<String>> mono = flux.collectList();
        System.out.println(String.join("", Objects.requireNonNull(mono.block())));
    }


    @Test
    public void test8() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "a";
        });
        Mono<String> mono = Mono.fromFuture(future);
        System.out.println(mono.block());
    }

    @SneakyThrows
    @Test
    public void test9() {
        Flux<Long> flux = Flux.just(System.currentTimeMillis());
        flux.subscribe(it->{
            System.out.println(it);
        });
        TimeUnit.SECONDS.sleep(1);
        flux.subscribe(it->{
            System.out.println(it);
        });

        Flux<Long> flux2 = Flux.defer(()->Flux.just(System.currentTimeMillis()));
        flux2.subscribe(it->{
            System.out.println(it);
        });
        TimeUnit.SECONDS.sleep(1);
        flux2.subscribe(it->{
            System.out.println(it);
        });


    }

}
