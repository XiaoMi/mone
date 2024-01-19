package run.mone.ultraman.test;

import com.google.common.util.concurrent.Monitor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2023/6/25 16:04
 */
@Slf4j
public class MonitorTest {


    @SneakyThrows
    @Test
    public void test1() {

        System.out.println(123);
        Monitor monitor = new Monitor();

        Mutable<String> m = new MutableObject<>("");

        Monitor.Guard guard = monitor.newGuard(() -> {
            return StringUtils.isNotEmpty(m.getValue());
        });

        Monitor.Guard guard2 = monitor.newGuard(() -> {
            return !StringUtils.isNotEmpty(m.getValue());
        });

        new Thread(() -> {
            try {
                monitor.enterWhen(guard);
                log.info(m.getValue());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                monitor.leave();
            }

        }).start();

        TimeUnit.SECONDS.sleep(1);

        new Thread(() -> {
            try {
                monitor.enterWhen(guard2);
                log.info(m.getValue());
                m.setValue("123");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                monitor.leave();
            }
        }).start();

        System.in.read();

    }

}
