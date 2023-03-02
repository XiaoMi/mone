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
            //不可读，不存在，则退出
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
            //不存在，不可写，则退出
            return "";
        }
        if (path.equals("/usr/local/etc/prometheus.yml")) {
            log.info("checkNullFile path: {},content: {}", path, content);
        }
        FileUtils.write(file, content);
        return "success";
    }

    //检验文件存在
    @SneakyThrows
    public static boolean IsHaveFile(String path) {
        log.info("FileUtil IsHaveFile path: {}", path);
        File file = new File(path);
        return file.exists();
    }

    //删除文件
    @SneakyThrows
    public static synchronized boolean DeleteFile(String path) {
        log.info("FileUtil DeleteFile path: {}", path);
        File file = new File(path);
        boolean delete = file.delete();
        return delete;
    }

    //检验文件可读性
    @SneakyThrows
    private static boolean isCanReadFile(File file) {
        log.info("FileUtil isCanReadFile file: {}", file.getAbsoluteFile());
        //文件不可读
        return file.canRead();
    }

    //检验文件可写性
    @SneakyThrows
    private static boolean isCanWriteFile(File file) {
        log.info("FileUtil isCanWriteFile file: {}", file.getAbsoluteFile());
        //文件不可写
        return file.canWrite();
    }

    //重命名文件
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
