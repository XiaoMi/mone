package run.mone.hive.reactor;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author goodjava@qq.com
 * @date 2025/4/5 21:16
 */
public class ReactorTest {


    @Test
    public void testOptional() {
        Optional<String> optional = Optional.of("abc");
        String v = optional.map(it->it.toUpperCase(Locale.ROOT)).orElse("ccc");
        System.out.println(v);
    }

    @Test
    public void testStream() {
        @NotNull List<Integer> list = Stream.of(1, 2, 3).map(it -> it + 1).filter(it -> it % 2 == 0).toList();
        System.out.println(list);
    }


    @SneakyThrows
    @Test
    public void test2() {
        Flux.create(sink->{
            sink.complete();
            sink.complete();
            System.out.println("complete");
        }).subscribe();

        System.in.read();
    }


    @Test
    public void testMono() {
        Mono.just("abc").subscribe(System.out::println);
    }

    @Test
    public void testFlux() {
        Flux.fromIterable(Lists.newArrayList("1", "2", "3")).map(it -> it + "^").subscribe(System.out::println);
    }

    @Test
    public void testSink() {
        Flux.create(sink -> {
            sink.next("a");
            sink.next("b");
            sink.next("c");
            sink.complete();
        }).subscribe(System.out::println, error -> {
            System.out.println(error.getMessage());
        }, () -> System.out.println("complated"));
    }

    @Test
    public void testEmpty() {
        Flux<Object> flux = Flux.empty();
        System.out.println(flux.hasElements().block());
    }

    @Test
    public void testPublishOn() {
        Flux.<Integer>create(sink -> {
            sink.next(1);
            sink.next(2);
            sink.next(3);
            sink.complete();
        }).publishOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(5))).map(it -> {
            System.out.println(Thread.currentThread().getName() + " " + it);
            return it + 1;
        }).subscribe(System.out::println);
    }

    @Test
    public void testDoOnNext() {
        Flux.fromIterable(Lists.newArrayList(1, 2, 3, 4)).doOnNext(it -> {
            System.out.println("it:" + it);
        }).subscribe(System.out::println);
    }

    @Test
    public void testLog() {
        Flux.fromStream(Lists.newArrayList(1, 2, 3).stream()).log().subscribe(System.out::println);
    }

    @Test
    public void testCollect() {
        List<Integer> list = Flux.fromIterable(Lists.newArrayList(1, 2, 3)).collectList().block();
        System.out.println(list);
    }


    @Test
    public void testSubscribeOn() {
        Mono.fromFuture(CompletableFuture.supplyAsync(() -> "abc")).subscribeOn(Schedulers.boundedElastic()).subscribe(System.out::println);
        System.out.println("finish");
    }

    @Test
    public void testBaseSubscriber() {
        Flux.range(0, 20)
                .log()
                //队列10
                .onBackpressureBuffer(10)
                .subscribe(new BaseSubscriber<Integer>() {
                    //初始请求5个
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) {
                        request(5);
                    }

                    @SneakyThrows
                    @Override
                    protected void hookOnNext(Integer value) {
                        //这里就可以加判断了,负载低就多处理,负载高就少处理(request)  可以解决cpu负载高,和内存溢出的问题
                        System.out.println(value);
                        //模拟处理慢
                        TimeUnit.SECONDS.sleep(1);
                        //再请求1个
                        request(1);
                    }
                });
    }


    @Test
    public void testOnErrorContinue() {
        Flux.range(0, 20).log()
                .map(it -> {
                    return 1 / it;
                }).onErrorContinue((error, it) -> {
                    System.out.println(error.getMessage() + " it:" + it);
                })
                .subscribe(it -> {
                    System.out.println(it);
                });
    }


    @Test
    public void testWindow() {
        Flux.range(0, 20).window(5).flatMap(window -> {
            return window.collectList();
        }).subscribe(it -> {
            System.out.println(it);
        });
    }

    @Test
    public void testZip() {
        Mono.zip(Mono.just("1"), Mono.just("2"), Mono.just("3")).map(tuple -> {
            return tuple.getT1() + ":" + tuple.getT2() + ":" + tuple.getT3();
        }).subscribe(it -> {
            System.out.println(it);
        });
    }



}
