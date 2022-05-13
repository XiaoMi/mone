package run.mone.disruptor.test;

import org.junit.Test;
import run.mone.disruptor.MapDisruptor;

import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/4/27 13:51
 */
public class DisruptorTest {


    /**
     * 当池子使用
     */
    @Test
    public void testPool() {
        MapDisruptor md = new MapDisruptor();
        md.start(v -> {
        }, 2);
        IntStream.range(0, 10).parallel().forEach(i -> {
            Map<String, Object> v = md.get(data -> {
                System.out.println("==>" + data);
                data.put("n", i);
            });
            System.out.println(v);
        });

    }


    @Test
    public void test1() {
        MapDisruptor md = new MapDisruptor();
        md.start(mapEvent -> {
            System.out.println(Thread.currentThread().getName());
            System.out.println(mapEvent.getData());
        }, 2);

        System.out.println("----");

        IntStream.range(0, 10).parallel().forEach(i -> {
            md.publishEvent(m -> {
                m.put("name", "zzy");
                m.put("id", i);
            });
        });

    }
}
