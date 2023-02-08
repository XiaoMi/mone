package com.xiaomi.youpin.docean.plugin.log;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import lombok.Data;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * @author goodjava@qq.com
 * @date 2022/12/11 18:28
 */
@Data
public class LogReader {

    private String filePath;

    public LogReader(String filePath) {
        this.filePath = filePath;
    }

    public int read(Function<String, Boolean> function, int skipLineNum) throws IOException {
        int num = Files.asCharSource(new File(filePath), Charset.forName("utf-8")).readLines(new LineProcessor<Integer>() {
            private int num = 0;

            @Override
            public boolean processLine(String line) {
                ++num;
                if (num <= skipLineNum) {
                    return true;
                }
                return function.apply(line);
            }

            @Override
            public Integer getResult() {
                return this.num;
            }
        });
        return num;
    }

}
