package com.xiaomi.mone.file;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
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

    private MoneRandomAccessFile raf;

    private ReadListener listener;

    @Setter
    private volatile boolean stop;

    @Setter
    private volatile boolean reOpen;

    @Setter
    private volatile boolean reFresh;

    @Getter
    private int beforePointerHashCode;

    private long pointer;

    //行号
    private long lineNumber;

    //每次读取时文件的最大偏移量
    private long maxPointer;

    private String md5;

    private static final int LINE_MAX_LENGTH = 50000;

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
            TimeUnit.SECONDS.sleep(5);
            //4kb
            this.raf = new MoneRandomAccessFile(file, "r", 1024 * 4);
            reOpen = false;
            reFresh = false;
        } catch (InterruptedException e) {
            log.error("open file InterruptedException", e);
        } catch (FileNotFoundException e) {
            log.error("open file FileNotFoundException", e);
        } catch (IOException e) {
            log.error("open file IOException", e);
        }
    }

    public void readLine() throws IOException {
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

            while (true) {
                String line = raf.getNextLine();
                if (null != line && lineNumber == 0 && pointer == 0) {
                    String hashLine = line.length() > 100 ? line.substring(0, 100) : line;
                    beforePointerHashCode = hashLine.hashCode();
                }
                //大行文件先临时截断
                line = lineCutOff(line);

                if (reFresh) {
                    break;
                }

                if (reOpen) {
                    pointer = 0;
                    lineNumber = 0;
                    break;
                }

                if (stop) {
                    break;
                }

                //文件内容被切割，重头开始采集内容
                if (contentHasCutting(line)) {
                    reOpen = true;
                    pointer = 0;
                    lineNumber = 0;
                    log.warn("file:{} content have been cut, goto reOpen file", file);
                    break;
                }

                if (listener.isContinue(line)) {
                    continue;
                }

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
            raf.close();
            if (stop) {
                break;
            }
        }
    }

    private String lineCutOff(String line) {
        if (null != line) {
            //todo 大行文件先临时截断
            if (line.length() > LINE_MAX_LENGTH) {
                line = line.substring(0, LINE_MAX_LENGTH);
            }
        }

        return line;
    }

    private boolean contentHasCutting(String line) throws IOException {
        if (null != line) {
            return false;
        }

        long currentFileMaxPointer;
        try {
            currentFileMaxPointer = raf.length();
            if (currentFileMaxPointer == 0L) {
                raf.getFD().sync();
                TimeUnit.MILLISECONDS.sleep(30);
                currentFileMaxPointer = raf.length();
            }
        } catch (IOException e) {
            log.error("get fileMaxPointer IOException", e);
            return false;
        } catch (InterruptedException e) {
            log.error("get fileMaxPointer InterruptedException", e);
            return false;
        }

        //针对大文件,排除掉局部内容删除的情况,更准确识别内容整体切割的场景（误判重复采集成本较高）
        long mPointer = maxPointer > 70000 ? maxPointer - 700 : maxPointer;
        if (currentFileMaxPointer < mPointer) {
            return true;
        }

        return false;
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
        StringBuilder sb = new StringBuilder(2 * digest.length);
        for(byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString().toUpperCase();
    }


}
