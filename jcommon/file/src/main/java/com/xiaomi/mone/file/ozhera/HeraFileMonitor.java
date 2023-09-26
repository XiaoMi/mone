package com.xiaomi.mone.file.ozhera;

import com.google.common.collect.Lists;
import com.xiaomi.mone.file.common.FileInfo;
import com.xiaomi.mone.file.common.FileInfoCache;
import com.xiaomi.mone.file.common.FileUtils;
import com.xiaomi.mone.file.common.Pair;
import com.xiaomi.mone.file.event.EventListener;
import com.xiaomi.mone.file.event.EventType;
import com.xiaomi.mone.file.event.FileEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 09:55
 */
@Slf4j
public class HeraFileMonitor {


    @Getter
    private ConcurrentHashMap<Object, HeraFile> map = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, HeraFile> fileMap = new ConcurrentHashMap<>();

    @Setter
    private EventListener listener;


    public HeraFileMonitor() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                List<Pair<String, Object>> remList = Lists.newArrayList();
                long now = System.currentTimeMillis();
                fileMap.values().forEach(it -> {
                    if (now - it.getUtime().get() >= TimeUnit.SECONDS.toMillis(5)) {
                        remList.add(Pair.of(it.getFileName(), it.getFileKey()));
                    }
                });
                remList.forEach(it -> {
                    log.info("remove file:{}", it.getKey());
                    fileMap.remove(it.getKey());
                    map.remove(it.getValue());
                });
            } catch (Throwable ex) {
                log.error(ex.getMessage(), ex);
            }
        }, 5, 10, TimeUnit.SECONDS);
    }

    public HeraFileMonitor(EventListener listener) {
        this();
        this.listener = listener;
    }

    public void reg(String path) throws IOException, InterruptedException {
        Path directory = Paths.get(path);
        File f = directory.toFile();

        Arrays.stream(f.listFiles()).forEach(it -> initFile(it));

        WatchService watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                Path modifiedFile = (Path) event.context();
                if (modifiedFile.getFileName().toString().startsWith(".")) {
                    continue;
                }
                String filePath = path + "" + modifiedFile.getFileName();
                log.info(event.kind() + " " + filePath);
                HeraFile hfile = fileMap.get(filePath);

                if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                    if (null == hfile) {
                        hfile = initFile(new File(filePath));
                    }
                    modify(hfile);
                }

                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                    fileMap.remove(filePath);
                    if (null != hfile) {
                        map.remove(hfile.getFileKey());
                        listener.onEvent(FileEvent.builder().type(EventType.delete).fileName(filePath).fileKey(hfile.getFileKey()).build());
                    }
                }

                if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                    File file = new File(path + "" + modifiedFile.getFileName());
                    Object k = FileUtils.fileKey(file);
                    if (map.containsKey(k)) {
                        log.info("change name " + map.get(k) + "--->" + file);
                        listener.onEvent(FileEvent.builder().fileKey(k).type(EventType.rename).build());
                    } else {
                        listener.onEvent(FileEvent.builder().type(EventType.create).fileName(file.getPath()).build());
                    }
                    HeraFile hf = HeraFile.builder().file(file).fileKey(k).fileName(filePath).build();
                    map.putIfAbsent(k, hf);
                    fileMap.put(filePath, hf);
                }
            }
            key.reset();
        }
    }

    private ReentrantLock lock = new ReentrantLock();

    private HeraFile initFile(File it) {
        if (it.isFile()) {
            String name = it.getName();
            if (name.startsWith(".")) {
                return null;
            }
            Object fileKey = FileUtils.fileKey(it);
            lock.lock();
            try {
                if (map.containsKey(fileKey)) {
                    return map.get(fileKey);
                }
                HeraFile hf = HeraFile.builder().file(it).fileKey(fileKey).fileName(it.getPath()).build();
                FileInfo fi = FileInfoCache.ins().get(fileKey.toString());
                long pointer = 0L;
                if (null != fi) {
                    pointer = fi.getPointer();
                }
                map.put(hf.getFileKey(), hf);
                fileMap.put(hf.getFileName(), hf);
                this.listener.onEvent(FileEvent.builder().pointer(pointer).type(EventType.init).fileName(hf.getFileName()).build());
                return hf;
            } finally {
                lock.unlock();
            }
        }
        return null;
    }


    private void modify(HeraFile hfile) {
        hfile.getUtime().set(System.currentTimeMillis());
        if (hfile.getFile().length() == 0) {
            listener.onEvent(FileEvent.builder().type(EventType.empty).fileName(hfile.getFileName()).fileKey(hfile.getFileKey()).build());
        } else {
            listener.onEvent(FileEvent.builder().type(EventType.modify).build());
        }
    }

}
