package run.mone.pool;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.function.Supplier;

/**
 * @author goodjava@qq.com
 * @date 2022/5/3 11:45
 */
@Slf4j
public class Pool<T> {

    private GenericObjectPool<T> pool = null;

    private int total = 0;

    private Supplier<T> supplier;

    public void init(int total, final Supplier<T> supplier) {
        this.supplier = supplier;
        this.total = total;
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(total);
        config.setMaxIdle(total);
        pool = new GenericObjectPool<T>(new BasePooledObjectFactory<T>() {
            @Override
            public T create() {
                return supplier.get();
            }

            @Override
            public PooledObject wrap(T obj) {
                PooledObject o = new DefaultPooledObject(obj);
                return o;
            }
        }, config);
    }


    @SneakyThrows
    public T borrow() {
        if (total <= 0) {
            return supplier.get();
        }
        return pool.borrowObject();
    }

    public void returnObject(T obj) {
        if (total > 0) {
            pool.returnObject(obj);
        }
    }

}
