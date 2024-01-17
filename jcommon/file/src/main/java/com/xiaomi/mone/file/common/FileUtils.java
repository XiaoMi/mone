package com.xiaomi.mone.file.common;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 10:42
 */
public abstract class FileUtils {

    @SneakyThrows
    public static Object fileKey(File file) {
        BasicFileAttributeView basicView = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
        BasicFileAttributes attr = basicView.readAttributes();
        if (null != attr && null != attr.fileKey()) {
            return attr.fileKey();
        }
        return file.getPath();
    }


}
