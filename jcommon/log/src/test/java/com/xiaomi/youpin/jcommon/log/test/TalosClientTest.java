package com.xiaomi.youpin.jcommon.log.test;

import com.google.gson.Gson;
import com.xiaomi.youpin.jcommon.log.LogRecord;
import com.xiaomi.youpin.jcommon.log.TalosClient;
import libthrift091.TException;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TalosClientTest {

    @Test
    public void testSend() throws TException, InterruptedException {
        TalosClient client = new TalosClient();
        client.init();

        int i = 1;
        while (true) {
            LogRecord record = new LogRecord();
            record.setAppName("appName");
            record.setTraceId(String.valueOf(i++));
            record.setTimestamp(System.currentTimeMillis());
            record.setLevel("INFO");
            record.setClassName("ABC");
            boolean res = client.sendMsg(new Gson().toJson(record));
            System.out.println(res);
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }
}
