package run.mone.pool.test;

import org.junit.Test;
import run.mone.pool.BeanMap;
import run.mone.pool.Pool;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2022/5/3 11:53
 */
public class PoolTest {

    @Test
    public void testPool() {
        Pool pool = new Pool();
        pool.init(0,()->{
            BeanMap m = new BeanMap();
            m.put("key",System.nanoTime());
            return m;
        });

        IntStream.range(1,10).parallel().forEach(i->{
            BeanMap m = pool.borrow();
            System.out.println(m);
            pool.returnObject(m);
        });
    }
}
