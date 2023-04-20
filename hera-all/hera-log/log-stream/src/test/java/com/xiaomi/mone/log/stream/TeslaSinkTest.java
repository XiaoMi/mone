package com.xiaomi.mone.log.stream;

import com.xiaomi.mone.log.stream.sink.TeslaSink;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/8 15:10
 */
@Slf4j
public class TeslaSinkTest {

    @Test
    public void test() {
        Ioc.ins().init("com.xiaomi");
        TeslaSink flinkService = Ioc.ins().getBean(TeslaSink.class);
        Map<String, Object> map = new HashMap<>();
        flinkService.execute(map);
    }
}
