package com.xiaomi.mone.file.ozhera;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 15:56
 */
@Data
@Builder
public class HeraFile {

    private File file;

    private Object fileKey;

    private String fileName;

    @Builder.Default
    private AtomicInteger state = new AtomicInteger(0);


    @Builder.Default
    private AtomicLong pointer = new AtomicLong();

    @Builder.Default
    private AtomicLong utime = new AtomicLong(System.currentTimeMillis());

    @Builder.Default
    private AtomicLong readTime = new AtomicLong(System.currentTimeMillis());
}
