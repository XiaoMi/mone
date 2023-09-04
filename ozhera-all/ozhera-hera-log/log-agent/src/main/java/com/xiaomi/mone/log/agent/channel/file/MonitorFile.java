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

import com.xiaomi.mone.log.api.enums.LogTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/8/4 16:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonitorFile {
    /**
     * Real file path address
     */
    private String realFilePath;
    /**
     * Expression for monitoring file changes, eg: /home/work/log/server.log.*
     */
    private String monitorFileExpress;
    /**
     * Generate a regular expression interpreter based on the expression for subsequent matching.
     */
    private Pattern filePattern;
    /**
     * The collection of individual files is completed and then ends.
     */
    private boolean collectOnce;

    /**
     * Log type, due to the special nature of OpenTelemetry logs, requires special handling when listening.
     */
    private LogTypeEnum logTypeEnum;

    public MonitorFile(String realFilePath, String monitorFileExpress, LogTypeEnum logTypeEnum, boolean collectOnce) {
        this.realFilePath = realFilePath;
        this.monitorFileExpress = monitorFileExpress;
        this.filePattern = Pattern.compile(monitorFileExpress);
        this.logTypeEnum = logTypeEnum;
        this.collectOnce = collectOnce;
    }

    public static MonitorFile of(String realFilePath, String monitorFileExpress, LogTypeEnum logTypeEnum, boolean collectOnce) {
        return new MonitorFile(realFilePath, monitorFileExpress, logTypeEnum, collectOnce);
    }

}
