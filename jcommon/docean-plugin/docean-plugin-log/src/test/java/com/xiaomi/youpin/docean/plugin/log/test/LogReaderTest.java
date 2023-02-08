package com.xiaomi.youpin.docean.plugin.log.test;

import com.xiaomi.youpin.docean.plugin.log.LogReader;
import org.junit.Test;

import java.io.IOException;

/**
 * @author goodjava@qq.com
 * @date 2022/12/11 20:51
 */
public class LogReaderTest {

    @Test
    public void testRead() throws IOException {
        LogReader logReader = new LogReader("/tmp/redolog");
        logReader.setFilePath("/tmp/redolog");
        int num = logReader.read(line -> {
            if (line.equals("6")) {
                return false;
            }
            System.out.println(line);
            return true;
        }, 0);
        System.out.println(num);
    }
}
