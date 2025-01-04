package com.xiaomi.mone.file;

import com.google.common.collect.Lists;
import com.xiaomi.mone.file.common.FileUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
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
public class LogFile implements ILogFile {

    @Getter
    private String file;

    private MoneRandomAccessFile raf;

    private ReadListener listener;

    @Setter
    private volatile boolean stop;

    @Setter
    private volatile boolean reOpen;

    @Setter
    private volatile boolean reFresh;

    private volatile boolean exceptionFinish;

    @Getter
    private int beforePointerHashCode;

    @Getter
    private volatile long pointer;

    //行号
    private long lineNumber;

    //每次读取时文件的最大偏移量
    @Getter
    private volatile long maxPointer;

    private String md5;

    //    private static final int LINE_MAX_LENGTH = 50000;

    public LogFile() {

    }

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
            //日志文件进行切分时，减少FileNotFoundException概率,这个应该删掉了,在使用前保证就好了，由于历史原因,降低了休眠时间
//            TimeUnit.SECONDS.sleep(1);
            //4kb
            this.raf = new MoneRandomAccessFile(file, "r", 1024 * 4);
            reOpen = false;
            reFresh = false;
//        } catch (InterruptedException e) {
//            log.error("open file InterruptedException", e);
        } catch (FileNotFoundException e) {
            log.error("open file FileNotFoundException", e);
        } catch (IOException e) {
            log.error("open file IOException", e);
        }
    }

    @Override
    public void readLine() throws Exception {
        while (true) {
            open();
            //兼容文件切换时，缓存的pointer
            try {
                log.info("open file:{},pointer:{},fileKey:{}", file, pointer, FileUtils.fileKey(new File(file)));
                if (pointer > raf.length()) {
                    pointer = 0;
                    lineNumber = 0;
                }
            } catch (Exception e) {
                log.error("file.length() IOException, file:{}", this.file, e);
            }
            raf.seek(pointer);
            log.info("start readLine file:{},pointer:{}", file, pointer);

            while (true) {
                String line = raf.getNextLine();

                if (null != line && lineNumber == 0 && pointer == 0) {
                    String hashLine = line.length() > 100 ? line.substring(0, 100) : line;
                    beforePointerHashCode = hashLine.hashCode();
                }
                //大行文件先临时截断
                line = lineCutOff(line);

                if (reFresh) {
                    log.info("readline reFresh:{},pointer:{},lineNumber:{},fileKey:{}", this.file, this.pointer, this.lineNumber, FileUtils.fileKey(new File(file)));
                    break;
                }

                if (reOpen) {
                    log.info("readline reOpen:{},pointer:{},lineNumber:{},fileKey:{}", this.file, this.pointer, this.lineNumber, FileUtils.fileKey(new File(file)));
                    pointer = 0;
                    lineNumber = 0;
                    break;
                }

                if (stop) {
                    log.info("readline stop:{},pointer:{},lineNumber:{},fileKey:{}", this.file, this.pointer, this.lineNumber, FileUtils.fileKey(new File(file)));
                    break;
                }

                //文件内容被切割，重头开始采集内容
                if (contentHasCutting(line)) {
                    reOpen = true;
                    pointer = 0;
                    lineNumber = 0;
                    log.info("readline file:{} content have been cut, goto reOpen file,pointer:{},lineNumber:{},fileKey:{}", file, pointer, lineNumber, FileUtils.fileKey(new File(file)));
                    break;
                }

                if (listener.isContinue(line)) {
                    log.debug("readline isBreak:{},pointer:{},lineNumber:{},fileKey:{}", this.file, this.pointer, this.lineNumber, FileUtils.fileKey(new File(file)));
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
                log.info("read file stop:{},pointer:{},lineNumber:{},fileKey:{}", this.file, this.pointer, this.lineNumber, FileUtils.fileKey(new File(file)));
                break;
            }
        }
    }

    @Override
    public void initLogFile(String file, ReadListener listener, long pointer, long lineNumber) {
        this.file = file;
        this.md5 = md5(file);
        this.listener = listener;
        this.pointer = pointer;
        this.lineNumber = lineNumber;
    }

    @Override
    public void setExceptionFinish() {
        exceptionFinish = true;
    }

    @Override
    public boolean getExceptionFinish() {
        return exceptionFinish;
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
            maxPointer = currentFileMaxPointer;
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
        for (byte b : digest) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString().toUpperCase();
    }


}
