package run.mone.local.docean.test;

import com.google.common.base.Stopwatch;
import lombok.SneakyThrows;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/6/5 11:17
 */
public class CommonTest {


    @SneakyThrows
    @Test
    public void testSw() {
        Stopwatch sw = Stopwatch.createStarted();
        TimeUnit.SECONDS.sleep(1);
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
        sw.reset().start();
        TimeUnit.SECONDS.sleep(2);
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }

}
