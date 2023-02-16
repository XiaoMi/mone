package com.xiaomi.mone.log.agent.channel.file;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.xiaomi.mone.file.LogFile;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;

@Slf4j
@Service
public class FileMonitor implements FileWatcher {

    @Override
    public void watch(String filePattern, List<FileAlterationMonitor> monitorList, Consumer<String> consumer) {
        List<String> watchList = Lists.newArrayList(filePattern);
        // 默认 遍历文件 间隔时间 5s
        FileAlterationMonitor monitor = new FileAlterationMonitor(5000);
        log.info("agent monitor files:{}", new Gson().toJson(watchList));
        for (String watch : watchList) {
            FileAlterationObserver observer = new FileAlterationObserver(new File(watch));
            observer.addListener(new FileListener(consumer));
            log.info("## agent monitor file:{}, filePattern:{}", watch, filePattern);
            monitor.addObserver(observer);
        }
        try {
            monitor.start();
            log.info("## agent monitor filePattern:{} started", filePattern);
            monitorList.add(monitor);
        } catch (Exception e) {
            log.error(String.format("agent file monitor start err,monitor filePattern:%s", filePattern), e);
        }
    }

    @Override
    public void watch(LogFile logFile) {

    }

    @Override
    public void onChange() {

    }
}
