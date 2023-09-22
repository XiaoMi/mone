package run.mone.pool.test;

import com.google.common.base.Stopwatch;
import org.junit.Test;
import run.mone.pool.BeanMap;
import run.mone.pool.Pool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/5/3 11:53
 */
public class PoolTest {

    @Test
    public void testPool() {
        Pool<BeanMap> pool = new Pool();
        pool.init(10000, () -> {
            BeanMap m = new BeanMap();
            m.put("key", System.nanoTime());
            return m;
        });

        int num = 100000;

        Stopwatch sw = Stopwatch.createStarted();
        IntStream.range(1, num).parallel().forEach(i -> {
            BeanMap m = pool.borrow();
//            System.out.println(m);
            pool.returnObject(m);
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));


        sw = Stopwatch.createStarted();
        IntStream.range(1, num).forEach(i -> {
            BeanMap bm = new BeanMap();
        });
        System.out.println(sw.elapsed(TimeUnit.MILLISECONDS));
    }
}
