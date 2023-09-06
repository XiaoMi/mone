package com.xiaomi.youpin.prometheus.agent.util;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;

@Slf4j
public class FileUtil {
    @SneakyThrows
    public static synchronized String LoadFile(String path) {
        log.info("FileUtil LoadFile path: {}", path);
        File file = new File(path);
        if (!file.exists() || !isCanReadFile(file)) {
            //Unreadable, non-existent, then exit.
            return "";
        }
        String content = FileUtils.readFileToString(file, "UTF-8");
        return content;
    }

    @SneakyThrows
    public static synchronized String WriteFile(String path, String content) {
        log.info("FileUtil WriteFile path: {}", path);
        File file = new File(path);
        if (!file.exists() || !isCanWriteFile(file)) {
            //Does not exist, cannot be written, then exit.
            return "";
        }
        if (path.equals("/usr/local/etc/prometheus.yml")) {
            log.info("checkNullFile path: {},content: {}", path, content);
        }
        FileUtils.write(file, content);
        return "success";
    }

    //Check file existence
    @SneakyThrows
    public static boolean IsHaveFile(String path) {
        log.info("FileUtil IsHaveFile path: {}", path);
        File file = new File(path);
        return file.exists();
    }

    //Delete file.
    @SneakyThrows
    public static synchronized boolean DeleteFile(String path) {
        log.info("FileUtil DeleteFile path: {}", path);
        File file = new File(path);
        boolean delete = file.delete();
        return delete;
    }

    //Check the readability of the document.
    @SneakyThrows
    private static boolean isCanReadFile(File file) {
        log.info("FileUtil isCanReadFile file: {}", file.getAbsoluteFile());
        //The file is unreadable.
        return file.canRead();
    }

    //Check file writability
    @SneakyThrows
    private static boolean isCanWriteFile(File file) {
        log.info("FileUtil isCanWriteFile file: {}", file.getAbsoluteFile());
        //The file is not writable.
        return file.canWrite();
    }

    //Rename file
    @SneakyThrows
    public static synchronized boolean RenameFile(String oldPath, String newPath) {
        log.info("FileUtil RenameFile oldPath: {},newPath: {}", oldPath, newPath);
        File file = new File(oldPath);
        if (!file.exists()) {
            return false;
        }
        boolean res = file.renameTo(new File(newPath));
        return res;
    }

    @SneakyThrows
    public static synchronized void GenerateFile(String path) {
        log.info("FileUtil GenerateFile path: {}", path);
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
    }

}
