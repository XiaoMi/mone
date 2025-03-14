package com.xiaomi.mone.file.ozhera;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/9/25 09:55
 */
@Slf4j
public class HeraFileMonitor {

    @Getter
    private ConcurrentHashMap<Object, HeraFile> map = new ConcurrentHashMap<>();

    @Getter
    private ConcurrentHashMap<String, HeraFile> fileMap = new ConcurrentHashMap<>();

    @Setter
    private EventListener listener;

    @Setter
    private volatile boolean stop;

    private Gson gson = new Gson();

    public HeraFileMonitor() {
        this(TimeUnit.SECONDS.toMillis(30));
    }

    public HeraFileMonitor(long removeTime) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {
                List<Pair<String, Object>> remList = Lists.newArrayList();
                long now = System.currentTimeMillis();
                fileMap.values().forEach(it -> {
                    if (now - it.getUtime().get() >= removeTime && now - it.getReadTime().get() >= removeTime) {
                        remList.add(Pair.of(it.getFileName(), it.getFileKey()));
                    }
                });

                remList.forEach(it -> {
                    log.info("remove file:{},fileKey:{}", it.getKey(), it.getValue());
                    fileMap.remove(it.getKey());
                    map.remove(it.getValue());
                    listener.remove(it.getValue());
                });
            } catch (Throwable ex) {
                log.error("remove file error", ex);
            }
        }, 5, 10, TimeUnit.SECONDS);
    }


    public HeraFileMonitor(EventListener listener) {
        this();
        this.listener = listener;
    }

    public void reg(String path, Predicate<String> predicate) throws IOException, InterruptedException {
        this.reg(path, predicate, 10);
    }

    public void reg(String path, Predicate<String> predicate, Integer maxFiles) throws IOException {
        Path directory = Paths.get(path);
        File f = directory.toFile();

        if (!f.exists()) {
            log.info("create directory:{}", directory);
            Files.createDirectories(directory);
        }
        List<File> fileList = Arrays.stream(Objects.requireNonNull(f.listFiles())).filter(it -> predicate.test(it.getPath())).toList();
        fileList = getRecentValidFilePaths(fileList, maxFiles);
        fileList.forEach(this::initFile);

        WatchService watchService = FileSystems.getDefault().newWatchService();
        directory.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_CREATE);
        while (!stop) {
            try {
                WatchKey key = watchService.take();
                try {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        Path modifiedFile = (Path) event.context();
                        String filePath = String.format("%s%s", path, modifiedFile.getFileName().toString());
                        if (!predicate.test(filePath) || modifiedFile.getFileName().toString().startsWith(".")) {
                            continue;
                        }
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
                            File file = new File(filePath);
                            Object k = FileUtils.fileKey(file);

                            if (map.containsKey(k)) {
                                log.info("change name " + map.get(k) + "--->" + file);
                                listener.onEvent(FileEvent.builder().fileKey(k).type(EventType.rename).build());
                            } else {
                                log.info("ENTRY_CREATE filePath:{},fileKey:{}", filePath, k);
                                HeraFile hf = HeraFile.builder().file(file).fileKey(k).fileName(filePath).build();
                                map.put(k, hf);
                                fileMap.put(filePath, hf);

                                listener.onEvent(FileEvent.builder().type(EventType.create).fileKey(k).fileName(file.getPath()).build());
                            }
                        }
                    }
                } catch (Exception e1) {
                    log.error("watchService poll events error", e1);
                } finally {
                    key.reset();
                }
            } catch (Exception e) {
                log.error("watchService error", e);
            }
        }
    }

    private ReentrantLock lock = new ReentrantLock();

    private HeraFile initFile(File it) {
        if (it.isFile()) {
            String name = it.getName();
            if (name.startsWith(".")) {
                return null;
            }
            lock.lock();
            try {
                Object fileKey = FileUtils.fileKey(it);
                if (map.containsKey(fileKey)) {
                    return map.get(fileKey);
                }
                HeraFile hf = HeraFile.builder().file(it).fileKey(fileKey).fileName(it.getPath()).build();
                FileInfo fi = FileInfoCache.ins().get(fileKey.toString());
                long pointer = 0L;
                if (null != fi && Objects.equals(it.getPath(), fi.getFileName())) {
                    pointer = fi.getPointer();
                }
                log.info("initFile fileName:{},fileKey:{},fi:{},pointer:{}", name, fileKey, fi, pointer);
                map.put(hf.getFileKey(), hf);
                fileMap.put(hf.getFileName(), hf);
                log.info("initFile hf:{},map size:{},fileMap size:{}", hf, map.size(), fileMap.size());
                this.listener.onEvent(FileEvent.builder()
                        .pointer(pointer)
                        .type(EventType.init)
                        .fileName(hf.getFileName())
                        .fileKey(hf.getFileKey())
                        .build());
                return hf;
            } catch (Exception e) {
                log.error("init file error,fileName:{}", name, e);
            } finally {
                lock.unlock();
            }
        }
        return null;
    }


    private void modify(HeraFile hfile) {
        if (null != hfile) {
            hfile.getUtime().set(System.currentTimeMillis());
            if (hfile.getFile().length() == 0) {
                listener.onEvent(FileEvent.builder().type(EventType.empty).fileName(hfile.getFileName()).fileKey(hfile.getFileKey()).build());
            } else {
                listener.onEvent(FileEvent.builder().type(EventType.modify).build());
            }
        }
    }

    public void stop() {
        this.stop = true;
        listener.stop();
    }

    protected List<File> getRecentValidFilePaths(List<File> filePaths, Integer maxFiles) {
        // filter out valid file objects
        List<File> validFiles = filePaths.stream()
                .filter(file -> file.exists() && file.isFile())
                .collect(Collectors.toList());

        if (validFiles.isEmpty()) {
            log.info("No valid files found in the provided list.");
            return Collections.emptyList();
        }
        // if there are more than 100 valid files, the top 100 are sorted in reverse order of last modified
        if (validFiles.size() > maxFiles) {
            validFiles = validFiles.stream()
                    .sorted(Comparator.comparingLong(File::lastModified).reversed())
                    .limit(maxFiles)
                    .toList();
        }

        return validFiles;
    }

}
