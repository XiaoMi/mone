/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
