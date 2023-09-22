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

package com.xiaomi.mone.file;

import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author goodjava@qq.com
 * @Date 2021/7/8 14:42
 */
public class LogFileTest {


    @Test
    public void testLog() throws IOException {
        LogFile log = new LogFile("/var/log/system.log", new ReadListener() {
            @Override
            public void onEvent(ReadEvent event) {
                System.out.println(event.getReadResult().getLines());
            }

            @Override
            public boolean isContinue(String line) {
                return null == line;
            }
        });
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.shutdown();
        }).start();
        log.readLine();
        System.in.read();
    }

    @Test
    public void testLogWS() throws IOException {
        LogFileWS log = new LogFileWS("D:\\t", new ReadListener() {
            @Override
            public void onEvent(ReadEvent event) {
                System.out.println(event.getReadResult().getLines());
            }

            @Override
            public boolean isContinue(String line) {
                return null == line;
            }
        });
        log.readLine();
    }


    class MyReadListener implements ReadListener {

        private MLog mLog = new MLog();

        @Override
        public void onEvent(ReadEvent event) {
            String m = event.getReadResult().getLines().get(0);
            System.out.println(m);
        }

        @Override
        public boolean isContinue(String line) {
            return null == line;
        }
    }


    @Test
    public void testLog2() throws IOException {
        LogFile log = new LogFile("/tmp/zzytest/zzytest/server.log", new MyReadListener());
        log.readLine();
    }


    @Test
    public void testReadFileCutting() throws IOException {
        LogFile log = new LogFile("/home/work/log/hera-operator/server.log", new MyReadListener());
        log.readLine();
        System.in.read();
    }
}
