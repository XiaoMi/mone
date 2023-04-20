package com.xiaomi.mone.log.agent;

import com.xiaomi.mone.log.agent.channel.listener.DefaultFileMonitorListener;
import org.junit.Test;

import java.io.IOException;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/4 19:31
 */
public class DefaultFileMonitorListenerTest {

    @Test
    public void testFileChange() throws IOException {
        DefaultFileMonitorListener defaultFileMonitorListener = new DefaultFileMonitorListener();
        System.in.read();
    }
}
