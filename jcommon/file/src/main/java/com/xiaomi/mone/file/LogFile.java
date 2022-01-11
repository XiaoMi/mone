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

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.DatatypeConverter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class LogFile {

    @Getter
    private final String file;

    private RandomAccessFile raf;

    private ReadListener listener;

    @Setter
    private volatile boolean stop;

    @Setter
    private volatile boolean reOpen;


    private long pointer;

    //行号
    private long lineNumber;

    private String md5;

    private static final int LINE_MAX_LENGTH = 25000;

    public LogFile(String file, ReadListener listener) {
        this.file = file;
        this.md5 = md5(file);
        this.listener = listener;
        this.pointer = readPointer();
    }

    public LogFile(String file, ReadListener listener, long pointer, long lineNumber) {
        this.file = file;
        this.md5 = md5(file);
        this.listener = listener;
        this.pointer = pointer;
        this.lineNumber = lineNumber;
    }

    private void open() {
        try {
            //日志文件进行切分时，减少FileNotFoundException概率
            TimeUnit.SECONDS.sleep(1);
            this.raf = new RandomAccessFile(file, "r");
            reOpen = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void readLine() throws IOException {
        while (true) {
            open();

            //兼容文件切换时，缓存的pointer
            if (pointer > raf.length()) {
                pointer = 0;
                lineNumber = 0;
            }
            raf.seek(pointer);

            while (true) {
                String line = raf.readLine();
                if (null != line) {
                    line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    //todo 大行文件先临时截断
                    if (line.length() > LINE_MAX_LENGTH) {
                        line = line.substring(0, LINE_MAX_LENGTH);
                    }
                }

                if (reOpen) {
                    pointer = 0;
                    lineNumber = 0;
                    break;
                }
                if (stop) {
                    break;
                }
                if (listener.isContinue(line)) {
                    continue;
                }

                pointer = raf.getFilePointer();
                Long maxPointer = null;
                try {
                    maxPointer = raf.length();
                } catch (IOException e) {
                    log.error("file.length() IOException, file:{}", this.file, e);
                }

                ReadResult readResult = new ReadResult();
                readResult.setLines(Lists.newArrayList(line));
                readResult.setPointer(pointer);
                readResult.setFileMaxPointer(maxPointer);
                readResult.setLineNumber(++lineNumber);
                ReadEvent event = new ReadEvent(readResult);

                listener.onEvent(event);
            }
            raf.close();
            if (stop) {
                break;
            }
        }
    }


    public void shutdown() {
        try {
            this.stop = true;
            Files.write(Paths.get("/tmp/" + this.md5), String.valueOf(this.pointer).getBytes());
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }


    public long readPointer() {
        try {
            byte[] data = Files.readAllBytes(Paths.get("/tmp/" + this.md5));
            return Long.valueOf(new String(data));
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        return 0;
    }


    @SneakyThrows
    public String md5(String msg) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(msg.getBytes());
        byte[] digest = md.digest();
        return DatatypeConverter
                .printHexBinary(digest).toUpperCase();
    }


}
