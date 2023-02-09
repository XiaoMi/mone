package com.xiaomi.mone.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
@Slf4j
public class FileUtils {

    private static ConcurrentHashMap<String, SoftReference<Pattern>> patternMap = new ConcurrentHashMap();

    public static ReadResult readFile(String file, long filePointer, int lineNum) throws IOException {
        return readFile(file, filePointer, lineNum, null);
    }


    public static ReadResult readFile(String file, long filePointer, int lineNum, String grepRegex) throws IOException {
        Pattern pattern = null;
        if (null != grepRegex && grepRegex.trim().length() > 0) {
            SoftReference<Pattern> softReference = patternMap.get(grepRegex.trim());
            if (null == softReference || null == softReference.get()) {
                pattern = Pattern.compile(grepRegex); //将正则表达式进行编译
                softReference = new SoftReference(pattern);
                patternMap.put(grepRegex.trim(), softReference);
            } else {
                pattern = softReference.get();
            }
        }

        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long length = raf.length();
            if (filePointer > length) {
                log.warn("filePointer > length");
                filePointer = 0;
            }
            //从文件未读取
            if (filePointer == -1) {
                filePointer = raf.length();
            }

            raf.seek(filePointer);
            String line = "";
            int n = 1;
            List<String> lines = new ArrayList<>();
            boolean over = true;
            while ((line = raf.readLine()) != null) {
                line = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                boolean match = true;
                if (null != pattern) {
                    Matcher m = pattern.matcher(line);
                    match = m.find();
                }

                if (match) {
                    lines.add(line);
                }
                if (++n > lineNum) {
                    over = false;
                    break;
                }
            }
            long pointer = raf.getFilePointer();
            ReadResult readResult = new ReadResult();
            readResult.setPointer(pointer);
            readResult.setLines(lines);
            readResult.setOver(over);
            return readResult;
        }

    }


    public static List<MoneFile> list(String path) throws IOException {
        return Files.list(Paths.get(path))
                .sorted()
                .map(it -> {
                    File file = it.toFile();
                    MoneFile mf = new MoneFile();
                    mf.setFile(file.isFile());
                    mf.setName(file.getName());
                    return mf;
                }).collect(Collectors.toList());
    }

}
