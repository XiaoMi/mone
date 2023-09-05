package com.xiaomi.mone.file;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class LogFileWS extends LogFile {

    @Getter
    private final String file;

    private MoneRandomAccessFile raf;

    private ReadListener listener;

    @Setter
    private volatile boolean stop;

    @Setter
    private volatile boolean reOpen;


    private long pointer;

    //行号
    private long lineNumber;

    private String md5;

    private static final int LINE_MAX_LENGTH = 50000;

    private WatchService ws;

    {
        try {
            ws = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LogFileWS(String file, ReadListener listener) {
        super(file, listener);
        this.file = file;
        this.md5 = md5(file);
        this.listener = listener;
        this.pointer = readPointer();
    }

    public LogFileWS(String file, ReadListener listener, long pointer, long lineNumber) {
        super(file, listener, pointer, lineNumber);
        this.file = file;
        this.md5 = md5(file);
        this.listener = listener;
        this.pointer = pointer;
        this.lineNumber = lineNumber;
    }

    private void open() {
//        try {
//            //日志文件进行切分时，减少FileNotFoundException概率
//            TimeUnit.SECONDS.sleep(100);
//            this.raf = new MoneRandomAccessFile(file, "r");
//            reOpen = false;
//        } catch (InterruptedException e) {
//            log.error("open file Exception", e);
//        } catch (FileNotFoundException e) {
//            log.error("open file Exception", e);
//        }
        try {
            this.raf = new MoneRandomAccessFile(file, "r", 512);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reOpen = false;
    }


    @Override
    public void readLine() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path p = Paths.get(file.substring(0, file.length() - 11));
        p.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        while (true) {
            open();
            //兼容文件切换时，缓存的pointer
            try {
                log.info("open file:{},pointer:{}", file, raf.getFilePointer());
                if (pointer > raf.length()) {
                    pointer = 0;
                    lineNumber = 0;
                }
            } catch (Exception e) {
                log.error("file.length() IOException, file:{}", this.file, e);
            }
            raf.seek(pointer);
            readFile:
            while (true) {
                WatchKey watchKey = null;
                try {
                    watchKey = watchService.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    continue;
                }
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for (WatchEvent<?> watchEvent : watchEvents) {
                    if (watchEvent.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
                        String line = raf.getNextLine();
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
                            break readFile;
                        }
                        if (stop) {
                            break readFile;
                        }
                        Long maxPointer = null;
                        try {
                            pointer = raf.getFilePointer();
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
                }
                watchKey.reset();
            }
            raf.close();
            if (stop) {
                break;
            }
        }
    }


    @Override
    public void shutdown() {
        try {
            this.stop = true;
            Files.write(Paths.get("/tmp/" + this.md5), String.valueOf(this.pointer).getBytes());
        } catch (Throwable ex) {
            log.error(ex.getMessage());
        }
    }


    @Override
    public long readPointer() {
        try {
            byte[] data = Files.readAllBytes(Paths.get("/tmp/" + this.md5));
            return Long.valueOf(new String(data));
        } catch (Throwable e) {
            log.error(e.getMessage());
        }
        return 0;
    }


    @Override
    @SneakyThrows
    public String md5(String msg) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(msg.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder(2 * digest.length);
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString().toUpperCase();
    }


}
