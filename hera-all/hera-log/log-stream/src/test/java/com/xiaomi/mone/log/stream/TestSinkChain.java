package com.xiaomi.mone.log.stream;

import com.xiaomi.mone.log.stream.sink.SinkChain;
import com.xiaomi.youpin.docean.Ioc;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.xiaomi.mone.log.stream.common.util.StreamUtils.getConfigFromNacos;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2023/8/7 15:45
 */
@Slf4j
public class TestSinkChain {

    private SinkChain sinkChain;

    @Before
    public void init() {
        getConfigFromNacos();
        Ioc.ins().init("com.xiaomi.mone.log.stream", "com.xiaomi.youpin.docean");
        sinkChain = Ioc.ins().getBean(SinkChain.class);
    }

    @Test
    public void testChain() {
        Map<String, Object> data = new HashMap();
        sinkChain.execute(data);
    }
}
