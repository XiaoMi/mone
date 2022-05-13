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
