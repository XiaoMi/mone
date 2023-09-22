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

import com.xiaomi.mone.file.LogFile;
import org.apache.commons.io.monitor.FileAlterationMonitor;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author shanwb
 * @date 2021-07-19
 */
public interface FileWatcher {
    /**
     * Watch directory file changes.
     *
     * @param logFile
     */
    void watch(LogFile logFile);

    /**
     * watch directory file changes
     *
     * @param filePattern
     * @param monitorList
     * @param consumer
     */
    void watch(String filePattern, List<FileAlterationMonitor> monitorList, Consumer<String> consumer);

    /**
     *
     */
    void onChange();

}
