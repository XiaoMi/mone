package com.xiaomi.mone.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

/**
 * @author shanwb
 * @date 2022-02-07
 */
public class MoneRandomAccessFile extends RandomAccessFile {
    public MoneRandomAccessFile(String name, String mode) throws FileNotFoundException {
        super(name, mode);
    }

    public MoneRandomAccessFile(File file, String mode) throws FileNotFoundException {
        super(file, mode);
    }

    public final String readLineV2() throws IOException {
        StringBuffer input = new StringBuffer();
        int c = -1;
        int cCount = 0;
        boolean eol = false;

        while (!eol) {
            switch (c = read()) {
                case -1:
                    //遇到文件末尾的场景，持续读取一段时间，缓解 异步日志输出读取到不完整行问题
                    if (cCount++ > 10) {
                        eol = true;
                    } else {
                        try {
                            TimeUnit.MILLISECONDS.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case '\n':
                    eol = true;
                    break;
                case '\r':
                    eol = true;
                    long cur = getFilePointer();
                    if ((read()) != '\n') {
                        seek(cur);
                    }
                    break;
                default:
                    input.append((char)c);
                    break;
            }
        }

        if ((c == -1) && (input.length() == 0)) {
            return null;
        }
        return input.toString();
    }
}
