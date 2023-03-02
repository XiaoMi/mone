package com.xiaomi.mone.log.agent.channel.file;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.function.Consumer;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class FileListener implements FileAlterationListener {

    private Consumer<String> consumer;

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        log.debug("FileListener onStart ,filepath:{}", fileAlterationObserver.getDirectory());
    }

    @Override
    public void onDirectoryCreate(File file) {
    }

    @Override
    public void onDirectoryChange(File file) {
    }

    @Override
    public void onDirectoryDelete(File file) {
    }

    @Override
    public void onFileCreate(File file) {
        log.info("onFileCreate:" + file.getAbsolutePath());
        consumer.accept(file.getAbsolutePath());
    }

    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {

    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        log.debug("FileListener onStop ,filepath:{}", fileAlterationObserver.getDirectory());
    }
}
