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
            List<String> m = mLog.append(event.getReadResult().getLines().get(0));
            if (m.size() > 0) {
                System.out.println("--->" + m);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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
        System.in.read();
    }
}
