/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
