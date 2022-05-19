package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.test.anno.TAnno;

/**
 * @author goodjava@qq.com
 * @date 6/6/21
 */
public interface IDemoDao {

    @TAnno
    String get();

    void destory();
}
