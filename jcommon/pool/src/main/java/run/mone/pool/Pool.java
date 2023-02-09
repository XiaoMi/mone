package run.mone.pool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author goodjava@qq.com
 * @date 2022/5/3 11:45
 */
@Slf4j
public class Pool {

    private static GenericObjectPool<BeanMap> pool = null;

    private int total = 0;

    private Supplier<BeanMap> supplier;

    public void init(int total, Supplier<BeanMap> supplier) {
        this.supplier = supplier;
        this.total = total;
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(total);
        config.setMaxIdle(total);
        pool = new GenericObjectPool(new BasePooledObjectFactory() {
            @Override
            public Object create() {
                return Pool.this.supplier.get();
            }

            @Override
            public PooledObject wrap(Object obj) {
                PooledObject o = new DefaultPooledObject(obj);
                return o;
            }
        }, config);
    }


    @SneakyThrows
    public BeanMap borrow() {
        if (total <= 0) {
            return supplier.get();
        }
        return pool.borrowObject();
    }

    public void returnObject(BeanMap obj) {
        if (total > 0) {
            pool.returnObject(obj);
        }
    }

}
