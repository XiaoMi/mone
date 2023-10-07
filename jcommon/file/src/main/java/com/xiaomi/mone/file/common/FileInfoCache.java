package com.xiaomi.mone.file.common;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2023/9/26 11:50
 */
public class FileInfoCache {


    private Cache<String, FileInfo> cache = CacheBuilder.newBuilder().maximumSize(10000).build();

    private Gson gson = new Gson();

    @Setter
    private String filePath = "/tmp/.ozhera_pointer";

    private static final class LazyHolder {
        private static final FileInfoCache ins = new FileInfoCache();
    }


    public static final FileInfoCache ins() {
        return LazyHolder.ins;
    }

    public void put(String key, FileInfo val) {
        cache.put(key, val);
    }


    public FileInfo get(String key) {
        return cache.getIfPresent(key);
    }

    @SneakyThrows
    public void shutdown() {
        String str = gson.toJson(cache.asMap());
        Files.write(Paths.get(filePath), str.getBytes());
    }

    @SneakyThrows
    public void load() {
        if (new File(filePath).exists()) {
            String str = new String(Files.readAllBytes(Paths.get(filePath)));
            Type typeOfT = new TypeToken<Map<String, FileInfo>>() {
            }.getType();
            Map<String, FileInfo> map = gson.fromJson(str, typeOfT);
            map.forEach((k, v) -> cache.put(k, v));
        }
    }

    @SneakyThrows
    public void load(String filePath) {
        this.filePath = filePath;
        this.load();
    }
}
