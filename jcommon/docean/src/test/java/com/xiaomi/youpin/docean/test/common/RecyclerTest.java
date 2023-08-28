package com.xiaomi.youpin.docean.test.common;

import com.xiaomi.youpin.docean.test.bo.M;
import io.netty.util.Recycler;
import org.junit.Test;

/**
 * @author goodjava@qq.com
 * @date 2023/8/26 10:24
 */
public class RecyclerTest {

    @Test
    public void testa() {
        Recycler<M> recycler = new Recycler<M>() {
            @Override
            protected M newObject(Handle<M> handle) {
                System.out.println("new");
                M m = new M();
                m.setHandle(handle);
                return m;
            }
        };
        M m = recycler.get();
        m.setName("zzy");
        System.out.println(m);
        m.getHandle().recycle(m);
        System.out.println(recycler.get());
    }
}
