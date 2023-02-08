package test.com.xiaomi.youpin.dubbo.test;

import com.xiaomi.youpin.dubbo.filter.TraceIdContext;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TraceIdContextTest {


    @Test
    public void testGetSpandId() throws InterruptedException {
        int id = TraceIdContext.ins().getSpanId("abc");
        System.out.println(id);
        System.out.println(TraceIdContext.ins().getSpanId("abc"));
        TraceIdContext.ins().remove("abc");
        System.out.println(TraceIdContext.ins().getSpanId("abc"));


        Executors.newFixedThreadPool(20).invokeAll(IntStream.range(0, 40).mapToObj(i ->
                (Callable<Void>) () -> {
                    System.out.println(TraceIdContext.ins().getSpanId("abc"));
                    return null;
                }
        ).collect(Collectors.toList()));
    }
}
