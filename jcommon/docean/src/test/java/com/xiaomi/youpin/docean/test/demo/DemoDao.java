package com.xiaomi.youpin.docean.test.demo;

import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.test.anno.TAnno;
import com.xiaomi.youpin.docean.test.anno.TBAnno;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2020/6/20
 */
@Component
@Slf4j
public class DemoDao implements IDemoDao {

    @TAnno
    @TBAnno
    public String get() {
        return "dao";
    }


    public void destory() {
        log.info("destory dao");
    }
}
