package run.mone.event.test;

import com.google.common.eventbus.Subscribe;
import lombok.SneakyThrows;
import org.junit.Test;
import run.mone.event.Event;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2022/5/16
 */
public class EventTest {

    @SneakyThrows
    @Test
    public void testEvent() {
        Event.ins().register(new Listener());
        Event.ins().post("abc");
        Event.ins().post("def");
        System.out.println("finish");
        Thread.currentThread().join();
    }

    class Listener {

        @SneakyThrows
        @Subscribe
        public void lisen(String str) {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(Thread.currentThread().getName());
            System.out.println(str);
        }

    }
}
